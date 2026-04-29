package com.sa.event_mng.modules.marketing.presentation.controller;

import com.sa.event_mng.modules.marketing.application.dto.request.VoucherRequest;
import com.sa.event_mng.shared.dto.ApiResponse;
import com.sa.event_mng.modules.marketing.application.dto.response.VoucherResponse;
import com.sa.event_mng.modules.marketing.application.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Khuyến mãi", description = "Quản lý mã giảm giá")
public class VoucherController {

    VoucherService voucherService;

    @PostMapping
    @Operation(summary = "Tạo mã giảm giá mới (ADMIN/ORGANIZER)")
    public ApiResponse<VoucherResponse> create(@RequestBody @Valid VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.createVoucher(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách mã giảm giá")
    public ApiResponse<Page<VoucherResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return ApiResponse.<Page<VoucherResponse>>builder()
                .result(voucherService.getAllVouchers(pageRequest))
                .build();
    }
    @GetMapping("/validate")
    @Operation(summary = "Kiểm tra mã giảm giá")
    public ApiResponse<Double> validateVoucher(
            @RequestParam String code,
            @RequestParam Double amount,
            @RequestParam(required = false) Long eventId) {
        return ApiResponse.<Double>builder()
                .result(voucherService.calculateDiscount(code, amount, eventId))
                .build();
    }
}
