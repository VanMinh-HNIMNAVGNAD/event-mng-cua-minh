package com.sa.event_mng.modules.ordering.presentation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sa.event_mng.modules.ordering.application.service.OrderService;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    OrderService orderService;

    @PostMapping("/payos-webhook")
    public void handlePayOSWebhook(@RequestBody Map<String, Object> body) {
        System.out.println("Webhook received: " + body);
        try {
            String code = (String) body.get("code");
            if ("00".equals(code)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null) {
                    Object orderCodeObj = data.get("orderCode");
                    Long orderCode = null;
                    if (orderCodeObj instanceof Integer) {
                        orderCode = ((Integer) orderCodeObj).longValue();
                    } else if (orderCodeObj instanceof Long) {
                        orderCode = (Long) orderCodeObj;
                    }
                    
                    if (orderCode != null) {
                        System.out.println("Processing payment for orderCode: " + orderCode);
                        orderService.completePaymentByOrderCode(orderCode);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
