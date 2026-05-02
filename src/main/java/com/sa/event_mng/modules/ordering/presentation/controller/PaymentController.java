package com.sa.event_mng.modules.ordering.presentation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sa.event_mng.modules.ordering.application.service.OrderService;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    OrderService orderService;

    @org.springframework.web.bind.annotation.GetMapping("/payos-webhook")
    public String validateWebhook() {
        return "Webhook is active!";
    }

    @GetMapping("/redirect")
    public ResponseEntity<String> redirectToDeepLink(@RequestParam(name = "orderCode") Long orderCode,
                                                     @RequestParam(name = "status", required = false, defaultValue = "success") String status) {
        try {
            String deepLink = "customer://payment-status?orderId=" + orderCode + "&status=" + status;

            String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'/>"
                    + "<meta http-equiv='refresh' content='0;url=" + deepLink + "'/>"
                    + "<script>window.location='" + deepLink + "';</script></head><body>"
                    + "<p>If you are not redirected, <a href='" + deepLink + "'>click here</a>.</p></body></html>";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(html, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing redirect");
        }
    }

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
