package com.sa.event_mng.modules.blog.domain.repository;

import com.sa.event_mng.modules.blog.domain.model.BlogTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {
    Optional<BlogTag> findBySlug(String slug);
    boolean existsByName(String name);
}
