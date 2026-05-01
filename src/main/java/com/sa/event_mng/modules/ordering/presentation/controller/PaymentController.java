package com.sa.event_mng.modules.ordering.presentation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    @PostMapping("/payos-webhook")
    public void handlePayOSWebhook(@RequestBody Object body) {
        if (body instanceof java.util.Map) {
            // Only process if it looks like a real webhook data
            // In a real app, you'd use ObjectMapper to convert Object to Webhook
            // For now, we just want to return 200 OK to PayOS
            try {
                // If you have ObjectMapper bean, you can use it here
                // but for the sake of "Saving" the webhook, just returning void (200 OK) is enough
                System.out.println("Webhook received: " + body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
