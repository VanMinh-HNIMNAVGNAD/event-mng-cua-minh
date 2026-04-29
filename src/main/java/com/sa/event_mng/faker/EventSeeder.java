package com.sa.event_mng.faker;

import com.sa.event_mng.modules.event.domain.model.Category;
import com.sa.event_mng.modules.event.domain.model.Event;
import com.sa.event_mng.modules.event.domain.model.EventStatus;
import com.sa.event_mng.modules.event.domain.repository.CategoryRepository;
import com.sa.event_mng.modules.event.domain.repository.EventRepository;
import com.sa.event_mng.modules.identity.domain.model.User;
import com.sa.event_mng.modules.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class EventSeeder {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private static final String[] LOCATIONS = {"Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Hải Phòng"};
    private static final String[] PROVINCES  = {"Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Hải Phòng"};
    private static final String[] NAME_PREFIXES = {
            "Đại nhạc hội", "Festival", "Hội thảo", "Triển lãm", "Gala", "Workshop",
            "Lễ hội", "Cuộc thi", "Chương trình", "Sự kiện", "Đêm nhạc", "Hội nghị"
    };
    private static final String[] NAME_SUFFIXES = {
            "mùa hè 2025", "đặc sắc", "không thể bỏ lỡ", "hoành tráng", "kỷ niệm",
            "thường niên", "lần đầu tiên", "quốc tế", "toàn quốc", "cộng đồng"
    };
    private static final String[] DESC_TEMPLATES = {
            "Sự kiện quy tụ hàng nghìn người tham gia, mang đến những trải nghiệm độc đáo và đáng nhớ.",
            "Chương trình được tổ chức bởi đội ngũ chuyên nghiệp với nhiều hoạt động hấp dẫn.",
            "Đây là cơ hội tuyệt vời để giao lưu, học hỏi và kết nối với cộng đồng.",
            "Sự kiện nổi bật với nhiều tiết mục đặc sắc, phù hợp cho mọi lứa tuổi.",
            "Tham gia để trải nghiệm không khí sôi động và những khoảnh khắc khó quên.",
            "Chương trình hứa hẹn mang lại nhiều bất ngờ thú vị cho người tham dự.",
            "Sự kiện quy mô lớn với sự tham gia của nhiều nghệ sĩ và diễn giả nổi tiếng.",
            "Đừng bỏ lỡ cơ hội trải nghiệm sự kiện được mong chờ nhất trong năm."
    };

    private final Random random = new Random();

    public void seed() {
        if (eventRepository.count() > 0) return;

        List<User> organizers = userRepository.findByRoles_Name("ORGANIZER");
        List<Category> categories = categoryRepository.findAll();
        if (organizers.isEmpty() || categories.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        int totalDays = 150;

        for (int i = 0; i < 500; i++) {
            LocalDateTime startTime = now.minusDays(totalDays)
                    .plusDays(random.nextInt(totalDays))
                    .withHour(8 + random.nextInt(12))
                    .withMinute(0);
            LocalDateTime endTime = startTime.plusHours(2 + random.nextInt(6));

            EventStatus status;
            if (startTime.isAfter(now))       status = EventStatus.UPCOMING;
            else if (endTime.isAfter(now))    status = EventStatus.OPENING;
            else                              status = EventStatus.COMPLETED;

            int locIdx = random.nextInt(LOCATIONS.length);
            Category cat = categories.get(random.nextInt(categories.size()));
            User organizer = organizers.get(random.nextInt(organizers.size()));

            eventRepository.save(Event.builder()
                    .name(NAME_PREFIXES[random.nextInt(NAME_PREFIXES.length)]
                            + " " + cat.getName()
                            + " " + NAME_SUFFIXES[random.nextInt(NAME_SUFFIXES.length)])
                    .category(cat)
                    .organizer(organizer)
                    .location(LOCATIONS[locIdx])
                    .province(PROVINCES[locIdx])
                    .startTime(startTime)
                    .endTime(endTime)
                    .saleStartDate(startTime.minusDays(30))
                    .saleEndDate(startTime.minusHours(1))
                    .description(DESC_TEMPLATES[random.nextInt(DESC_TEMPLATES.length)])
                    .status(status)
                    .build());
        }
    }
}
