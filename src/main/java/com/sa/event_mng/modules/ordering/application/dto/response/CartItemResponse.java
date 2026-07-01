package com.sa.event_mng.modules.ordering.application.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long ticketTypeId;
    private String ticketTypeName;
    private String eventName;
    private Long eventId;
    private String eventImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private java.time.LocalDateTime saleEndDate;
}
