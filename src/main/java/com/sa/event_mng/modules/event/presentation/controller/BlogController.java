package com.sa.event_mng.modules.event.presentation.controller;

import com.sa.event_mng.shared.dto.ApiResponse;
import com.sa.event_mng.modules.event.application.dto.response.BlogEventResponse;
import com.sa.event_mng.modules.event.application.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Blog", description = "Các API phục vụ trang tin tức và hướng dẫn")
public class BlogController {

    EventService eventService;

    @GetMapping("/news")
    @Operation(summary = "Lấy dữ liệu tin tức sự kiện cho trang Blog")
    public ApiResponse<List<BlogEventResponse>> getBlogNews() {
        return ApiResponse.<List<BlogEventResponse>>builder()
                .result(eventService.getBlogNews())
                .build();
    }
}
