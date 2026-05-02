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
        try {
            System.out.println("DEBUG: [WEBHOOK] Received call from PayOS. Body: " + body);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            
            if (data != null) {
                Object orderCodeObj = data.get("orderCode");
                System.out.println("DEBUG: [WEBHOOK] OrderCode detected: " + orderCodeObj);
                
                if (orderCodeObj != null) {
                    Long orderCode = Long.valueOf(orderCodeObj.toString());
                    System.out.println("DEBUG: [WEBHOOK] Triggering OrderService.completePaymentByOrderCode for #" + orderCode);
                    orderService.completePaymentByOrderCode(orderCode);
                    System.out.println("DEBUG: [WEBHOOK] Process completed for #" + orderCode);
                }
            } else {
                System.out.println("DEBUG: [WEBHOOK] Data is not a Map or missing.");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: [WEBHOOK] Failed to process. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
