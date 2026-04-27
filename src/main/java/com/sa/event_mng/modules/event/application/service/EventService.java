package com.sa.event_mng.modules.event.application.service;

import com.sa.event_mng.modules.event.application.dto.request.EventRequest;
import com.sa.event_mng.modules.event.application.dto.response.EventResponse;
import com.sa.event_mng.modules.event.application.dto.response.OrganizerStatsResponse;
import com.sa.event_mng.modules.event.application.dto.response.BlogEventResponse;
import com.sa.event_mng.shared.exception.AppException;
import com.sa.event_mng.shared.exception.ErrorCode;
import com.sa.event_mng.modules.event.application.mapper.EventMapper;
import com.sa.event_mng.modules.event.domain.model.*;
import com.sa.event_mng.modules.event.domain.repository.*;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import com.sa.event_mng.modules.event.infrastructure.specification.EventSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventService {

    EventRepository eventRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    EventMapper eventMapper;
    StatisticsRepository statisticsRepository;
    TicketTypeRepository ticketTypeRepository;

    @Value("${app.file.base-url}")
    @lombok.experimental.NonFinal
    String fileBaseUrl;

    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse create(EventRequest request) {
        log.info("Đang tạo sự kiện mới kèm vé: Name={}", request.getName());
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Event event = Event.builder()
                .name(request.getName())
                .category(category)
                .organizer(organizer)
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .saleStartDate(request.getSaleStartDate())
                .saleEndDate(request.getSaleEndDate())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : EventStatus.PENDING)
                .build();

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<EventImage> images = saveImages(request.getFiles(), event);
            event.setImages(images);
        }

        Event savedEvent = eventRepository.save(event);

        if (request.getTicketTypes() != null && !request.getTicketTypes().isEmpty()) {
            request.getTicketTypes().forEach(ttReq -> {
                TicketType tt = TicketType.builder()
                        .event(savedEvent)
                        .name(ttReq.getName())
                        .price(ttReq.getPrice())
                        .totalQuantity(ttReq.getTotalQuantity())
                        .remainingQuantity(ttReq.getTotalQuantity())
                        .description(ttReq.getDescription())
                        .build();
                ticketTypeRepository.save(tt);
            });
        }

        return eventMapper.toEventResponse(savedEvent);
    }

    public Page<EventResponse> getAllPublished(
        String search, 
        String province, 
        java.math.BigDecimal minPrice, 
        java.math.BigDecimal maxPrice, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        PageRequest pageRequest) {
            
            List<EventStatus> activeStatuses = List.of(
                            EventStatus.UPCOMING,
                            EventStatus.OPENING,
                            EventStatus.CLOSED
            );
            
            Specification<Event> spec = EventSpecification.filterEvents(
                search, province, minPrice, maxPrice, startDate, endDate, activeStatuses
            );

            Page<Event> events = eventRepository.findAll(spec, pageRequest);
            return events.map(eventMapper::toEventResponse);
    }

    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
        return eventMapper.toEventResponse(event);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ORGANIZER') and @securityCustom.isOwner(#id, authentication))")
    public EventResponse update(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
        event.setName(request.getName());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setSaleStartDate(request.getSaleStartDate());
        event.setSaleEndDate(request.getSaleEndDate());
        event.setDescription(request.getDescription());
        if (request.getStatus() != null)
            event.setStatus(request.getStatus());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            event.setCategory(category);
        }

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<EventImage> newImages = saveImages(request.getFiles(), event);
            if (event.getImages() == null) {
                event.setImages(new ArrayList<>());
            }
            event.getImages().addAll(newImages);
        }

        return eventMapper.toEventResponse(eventRepository.save(event));
    }

    private List<EventImage> saveImages(List<MultipartFile> files, Event event) {
        List<EventImage> images = new ArrayList<>();
        File uploadDir = new File("uploads/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty())
                continue;
            try {
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File destinationFile = new File(
                        uploadDir.getAbsolutePath() + File.separator + filename);
                file.transferTo(destinationFile);
                String imageUrl = fileBaseUrl + "/" + filename;
                images.add(EventImage.builder().imageUrl(imageUrl).event(event).build());
            } catch (IOException e) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }
        return images;
    }

    public Page<EventResponse> getAllForAdmin(String search, String status, PageRequest pageRequest) {
        Page<Event> events;
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        if (hasSearch && hasStatus) {
            events = eventRepository.findByNameContainingIgnoreCaseAndStatus(
                    search, EventStatus.valueOf(status), pageRequest);
        } else if (hasSearch) {
            events = eventRepository.findByNameContainingIgnoreCase(search, pageRequest);
        } else if (hasStatus) {
            events = eventRepository.findByStatus(EventStatus.valueOf(status), pageRequest);
        } else {
            events = eventRepository.findAll(pageRequest);
        }
        return events.map(eventMapper::toEventResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse updateStatus(Long id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
        
        event.setStatus(status);
        
        return eventMapper.toEventResponse(eventRepository.save(event));
    }

    public Page<EventResponse> getMyEvents(PageRequest pageRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return eventRepository.findByOrganizerId(user.getId(), pageRequest)
                .map(eventMapper::toEventResponse);
    }

    public OrganizerStatsResponse getOrganizerStats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Event> myEvents = eventRepository.findByOrganizerId(organizer.getId());
        
        List<OrganizerStatsResponse.EventStat> eventStats = new ArrayList<>();
        double totalRev = 0;
        long totalSold = 0;

        for (Event event : myEvents) {
            long sold = event.getTicketTypes().stream()
                    .mapToLong(tt -> tt.getTotalQuantity() - tt.getRemainingQuantity())
                    .sum();

            double rev = event.getTicketTypes().stream()
                    .mapToDouble(tt -> (tt.getTotalQuantity() - tt.getRemainingQuantity()) * tt.getPrice().doubleValue())
                    .sum();
            
            long totalTickets = event.getTicketTypes().stream()
                    .mapToLong(tt -> tt.getTotalQuantity())
                    .sum();

            eventStats.add(OrganizerStatsResponse.EventStat.builder()
                    .eventId(event.getId())
                    .eventName(event.getName())
                    .totalTickets(totalTickets)
                    .ticketsSold(sold)
                    .revenue(rev)
                    .sellThroughRate(totalTickets > 0 ? (double) sold / totalTickets * 100 : 0)
                    .status(event.getStatus().name())
                    .imageUrl(event.getImages() != null && !event.getImages().isEmpty() ? event.getImages().get(0).getImageUrl() : null)
                    .build());
            
            totalRev += rev;
            totalSold += sold;
        }

        List<MonthlyRevenueProjection> dbStats = statisticsRepository.findMonthlyRevenueOrganizer(organizer.getId());
        List<OrganizerStatsResponse.MonthlyRevenue> monthlyRevenues = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            int year = date.getYear();
            int month = date.getMonthValue();
            
            java.math.BigDecimal monthlyRev = dbStats.stream()
                    .filter(p -> p.getYear() == year && p.getMonth() == month)
                    .map(MonthlyRevenueProjection::getRevenue)
                    .findFirst()
                    .orElse(java.math.BigDecimal.ZERO);
            
            monthlyRevenues.add(new OrganizerStatsResponse.MonthlyRevenue(year, month, monthlyRev));
        }

        return OrganizerStatsResponse.builder()
                .totalEvents(myEvents.size())
                .totalTicketsSold(totalSold)
                .totalRevenue(totalRev)
                .eventStats(eventStats)
                .monthlyRevenues(monthlyRevenues)
                .build();
    }

    public List<BlogEventResponse> getBlogNews() {
        LocalDateTime now = LocalDateTime.now();

        // Lấy tất cả sự kiện đang hoạt động (loại trừ COMPLETED và CANCELLED)
        // Đồng thời loại bỏ sự kiện đã kết thúc (endTime đã qua)
        List<EventStatus> excludedStatuses = List.of(EventStatus.COMPLETED, EventStatus.CANCELLED);

        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> !excludedStatuses.contains(e.getStatus()))
                .filter(e -> e.getEndTime() == null || e.getEndTime().isAfter(now))
                .sorted(Comparator.comparing(Event::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return events.stream().map(e -> BlogEventResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .location(e.getLocation())
                .province(e.getProvince())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .saleStartDate(e.getSaleStartDate())
                .saleEndDate(e.getSaleEndDate())
                .descriptionStatus(e.getDescription() != null && !e.getDescription().isBlank()
                        ? "Mô tả: " + e.getDescription() : "")
                .imageUrl(e.getImages() != null && !e.getImages().isEmpty()
                        ? e.getImages().get(0).getImageUrl() : null)
                .build()
        ).collect(Collectors.toList());
    }
}
