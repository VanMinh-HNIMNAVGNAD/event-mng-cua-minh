package com.sa.event_mng.faker;

import com.sa.event_mng.modules.blog.domain.model.BlogTag;
import com.sa.event_mng.modules.blog.domain.repository.BlogTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlogTagSeeder {

    private final BlogTagRepository blogTagRepository;

    private static final List<String[]> TAGS = List.of(
            new String[]{"Âm nhạc", "am-nhac"},
            new String[]{"Thể thao", "the-thao"},
            new String[]{"Nghệ thuật", "nghe-thuat"},
            new String[]{"Công nghệ", "cong-nghe"},
            new String[]{"Ẩm thực", "am-thuc"},
            new String[]{"Du lịch", "du-lich"},
            new String[]{"Giáo dục", "giao-duc"},
            new String[]{"Sức khỏe", "suc-khoe"},
            new String[]{"Kinh doanh", "kinh-doanh"},
            new String[]{"Giải trí", "giai-tri"}
    );

    public void seed() {
        for (String[] tag : TAGS) {
            if (!blogTagRepository.existsByName(tag[0])) {
                blogTagRepository.save(BlogTag.builder()
                        .name(tag[0])
                        .slug(tag[1])
                        .build());
            }
        }
    }
}
