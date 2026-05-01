package com.sa.event_mng.modules.blog.presentation.controller;

import com.sa.event_mng.modules.blog.application.dto.request.BlogPostRequest;
import com.sa.event_mng.modules.blog.application.dto.request.BlogTagRequest;
import com.sa.event_mng.modules.blog.application.dto.response.BlogPostResponse;
import com.sa.event_mng.modules.blog.application.dto.response.BlogTagResponse;
import com.sa.event_mng.modules.blog.application.service.BlogService;
import com.sa.event_mng.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Blog", description = "Các API phục vụ trang tin tức và bài viết")
public class BlogController {

    BlogService blogService;

    // ==================== PUBLIC ====================

    @GetMapping("/posts")
    @Operation(summary = "Lấy danh sách bài viết đã xuất bản")
    public ApiResponse<Page<BlogPostResponse>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<BlogPostResponse>>builder()
                .result(blogService.getPublishedPosts(page, size))
                .build();
    }

    @GetMapping("/posts/{slug}")
    @Operation(summary = "Lấy chi tiết bài viết theo slug")
    public ApiResponse<BlogPostResponse> getPostBySlug(@PathVariable String slug) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.getPostBySlug(slug))
                .build();
    }

    @GetMapping("/tags")
    @Operation(summary = "Lấy danh sách tất cả tag")
    public ApiResponse<List<BlogTagResponse>> getAllTags() {
        return ApiResponse.<List<BlogTagResponse>>builder()
                .result(blogService.getAllTags())
                .build();
    }

    // ==================== ORGANIZER ====================

    @GetMapping("/organizer/posts")
    @Operation(summary = "Organizer: Lấy danh sách bài viết của mình")
    public ApiResponse<Page<BlogPostResponse>> organizerGetMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<BlogPostResponse>>builder()
                .result(blogService.organizerGetMyPosts(page, size))
                .build();
    }

    @PostMapping("/organizer/posts")
    @Operation(summary = "Organizer: Tạo bài viết mới (chờ admin duyệt)")
    public ApiResponse<BlogPostResponse> organizerCreatePost(@Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.organizerCreatePost(request))
                .build();
    }

    @PutMapping("/organizer/posts/{id}")
    @Operation(summary = "Organizer: Sửa bài viết của mình (chỉ khi chưa được duyệt)")
    public ApiResponse<BlogPostResponse> organizerUpdatePost(@PathVariable Long id,
                                                              @Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.organizerUpdatePost(id, request))
                .build();
    }

    @DeleteMapping("/organizer/posts/{id}")
    @Operation(summary = "Organizer: Xóa bài viết của mình (chỉ khi chưa được duyệt)")
    public ApiResponse<Void> organizerDeletePost(@PathVariable Long id) {
        blogService.organizerDeletePost(id);
        return ApiResponse.<Void>builder().message("Xóa bài viết thành công").build();
    }

    // ==================== ADMIN ====================

    @GetMapping("/admin/posts")
    @Operation(summary = "Admin: Lấy tất cả bài viết (mọi trạng thái)")
    public ApiResponse<Page<BlogPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<BlogPostResponse>>builder()
                .result(blogService.getAllPosts(page, size))
                .build();
    }

    @PostMapping("/admin/posts")
    @Operation(summary = "Admin: Tạo bài viết mới")
    public ApiResponse<BlogPostResponse> createPost(@Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.createPost(request))
                .build();
    }

    @PutMapping("/admin/posts/{id}")
    @Operation(summary = "Admin: Cập nhật bài viết")
    public ApiResponse<BlogPostResponse> updatePost(@PathVariable Long id,
                                                     @Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.updatePost(id, request))
                .build();
    }

    @PatchMapping("/admin/posts/{id}/publish")
    @Operation(summary = "Admin: Duyệt và xuất bản bài viết")
    public ApiResponse<BlogPostResponse> publishPost(@PathVariable Long id) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.publishPost(id))
                .build();
    }

    @PatchMapping("/admin/posts/{id}/reject")
    @Operation(summary = "Admin: Từ chối bài viết")
    public ApiResponse<BlogPostResponse> rejectPost(@PathVariable Long id) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.rejectPost(id))
                .build();
    }

    @PatchMapping("/admin/posts/{id}/archive")
    @Operation(summary = "Admin: Lưu trữ bài viết")
    public ApiResponse<BlogPostResponse> archivePost(@PathVariable Long id) {
        return ApiResponse.<BlogPostResponse>builder()
                .result(blogService.archivePost(id))
                .build();
    }

    @DeleteMapping("/admin/posts/{id}")
    @Operation(summary = "Admin: Xóa bài viết")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        blogService.deletePost(id);
        return ApiResponse.<Void>builder().message("Xóa bài viết thành công").build();
    }

    @PostMapping("/admin/tags")
    @Operation(summary = "Admin: Tạo tag mới")
    public ApiResponse<BlogTagResponse> createTag(@Valid @RequestBody BlogTagRequest request) {
        return ApiResponse.<BlogTagResponse>builder()
                .result(blogService.createTag(request))
                .build();
    }
}
