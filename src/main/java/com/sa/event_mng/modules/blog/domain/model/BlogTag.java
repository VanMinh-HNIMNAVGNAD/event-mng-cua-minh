package com.sa.event_mng.modules.blog.domain.model;

import com.sa.event_mng.shared.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blog_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;
}
