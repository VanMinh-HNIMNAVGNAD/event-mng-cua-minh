package com.sa.event_mng.shared.presentation.controller;

import com.sa.event_mng.shared.infrastructure.cloudinary.CloudinaryService;
import com.sa.event_mng.shared.dto.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<String>builder()
                .result(cloudinaryService.uploadFile(file))
                .build();
    }
}
