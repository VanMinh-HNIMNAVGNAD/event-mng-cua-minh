package com.sa.event_mng.faker;

import com.sa.event_mng.modules.event.domain.model.Category;
import com.sa.event_mng.modules.event.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategorySeeder {

    private final CategoryRepository categoryRepository;

    public void seed() {
        if (categoryRepository.count() > 0) return;
        List<Category> cats = List.of(
                Category.builder().name("Âm nhạc").description("Âm nhạc, concert").build(),
                Category.builder().name("Thể thao").description("Sự kiện thể thao").build(),
                Category.builder().name("Công nghệ").description("Workshop & tech").build(),
                Category.builder().name("Nghệ thuật").description("Triển lãm, sân khấu, mỹ thuật").build(),
                Category.builder().name("Ẩm thực").description("Lễ hội ẩm thực, food tour").build(),
                Category.builder().name("Giáo dục").description("Hội thảo, khóa học, seminar").build(),
                Category.builder().name("Du lịch").description("Tour, khám phá địa danh").build(),
                Category.builder().name("Kinh doanh").description("Networking, hội nghị doanh nghiệp").build(),
                Category.builder().name("Sức khỏe").description("Yoga, thiền, wellness").build(),
                Category.builder().name("Cộng đồng").description("Từ thiện, tình nguyện, giao lưu").build()
        );
        categoryRepository.saveAll(cats);
    }
}
