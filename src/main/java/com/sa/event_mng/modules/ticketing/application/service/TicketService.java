package com.sa.event_mng.modules.ticketing.application.service;

import com.sa.event_mng.modules.ticketing.application.dto.response.TicketResponse;
import com.sa.event_mng.shared.exception.AppException;
import com.sa.event_mng.shared.exception.ErrorCode;
import com.sa.event_mng.modules.ticketing.application.mapper.TicketMapper;
import com.sa.event_mng.modules.ticketing.domain.model.Ticket;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.model.enums.TicketStatus;
import com.sa.event_mng.modules.ticketing.domain.repository.TicketRepository;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {

    TicketRepository ticketRepository;
    UserRepository userRepository;
    TicketMapper ticketMapper;

    public List<TicketResponse> getMyTickets() {
        User user = getCurrentUser();
        return ticketRepository.findAll().stream()
                .filter(t -> t.getOrder().getCustomer().getId().equals(user.getId()))
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('STAFF')")
    public TicketResponse checkIn(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_INVALID));

        User user = getCurrentUser();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

        Long eventOrganizerId = ticket.getTicketType().getEvent().getOrganizer().getId();

        if (!isAdmin) {
            if (isStaff) {
                if (user.getOrganizer() == null || !user.getOrganizer().getId().equals(eventOrganizerId)) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            } else {
                if (!user.getId().equals(eventOrganizerId)) {
                    throw new AppException(ErrorCode.TICKET_NOT_OWNED);
                }
            }
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            throw new AppException(ErrorCode.TICKET_USED);
        }

        if (ticket.getStatus() != TicketStatus.VALID) {
            throw new AppException(ErrorCode.TICKET_INVALID);
        }

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now());

        return ticketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}
