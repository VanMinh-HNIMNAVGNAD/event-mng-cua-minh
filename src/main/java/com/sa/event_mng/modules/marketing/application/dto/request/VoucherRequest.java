package com.sa.event_mng.modules.marketing.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {
    
    @NotBlank(message = "Mã voucher không được để trống")
    String code;

    @NotBlank(message = "Loại giảm giá phải là PERCENTAGE hoặc AMOUNT")
    String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    BigDecimal amount;

    BigDecimal maxDiscount;

    BigDecimal minOrderAmount;

    Integer quantity;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    LocalDateTime endDate;

    Long eventId; // Nếu null thì áp dụng cho mọi sự kiện
}
