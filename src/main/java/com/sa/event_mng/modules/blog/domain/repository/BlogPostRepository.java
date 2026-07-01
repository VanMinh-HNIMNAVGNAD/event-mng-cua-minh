package com.sa.event_mng.modules.blog.domain.repository;

import com.sa.event_mng.modules.blog.domain.model.BlogPost;
import com.sa.event_mng.modules.blog.domain.model.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);
    Page<BlogPost> findByAuthorId(Long authorId, Pageable pageable);
    Optional<BlogPost> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
