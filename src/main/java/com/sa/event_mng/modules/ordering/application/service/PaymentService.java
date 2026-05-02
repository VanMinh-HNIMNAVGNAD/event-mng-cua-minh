package com.sa.event_mng.modules.ordering.application.service;

import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.ordering.domain.repository.OrderRepository;
import com.sa.event_mng.shared.exception.AppException;
import com.sa.event_mng.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.Webhook;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PayOS payOS;
    OrderRepository orderRepository;
    
    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    @lombok.experimental.NonFinal
    OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${payos.client-id}")
    @lombok.experimental.NonFinal
    String clientId;

    @org.springframework.beans.factory.annotation.Value("${payos.api-key}")
    @lombok.experimental.NonFinal
    String apiKey;

    @org.springframework.beans.factory.annotation.Value("${payos.checksum-key}")
    @lombok.experimental.NonFinal
    String checksumKey;

    public String createPayOSPaymentLink(Order order) throws Exception {
        String description = "Thanh toan #" + order.getOrderCode();
        if (description.length() > 25) {
            description = "TT don hang " + order.getOrderCode();
        }
        
        String returnUrl = "http://localhost:5175/payment/success";
        String cancelUrl = "http://localhost:5175/payment/cancel";

        // 1. Prepare data for signature
        long amount = order.getTotalAmount().longValue();
        long orderCode = order.getOrderCode();
        
        // PayOS requires fields in alphabetical order for signature
        String signatureData = "amount=" + amount +
                "&cancelUrl=" + cancelUrl +
                "&description=" + description +
                "&orderCode=" + orderCode +
                "&returnUrl=" + returnUrl;

        String signature = hmacSHA256(signatureData, checksumKey);

        // 2. Create Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", orderCode);
        body.put("amount", amount);
        body.put("description", description);
        body.put("cancelUrl", cancelUrl);
        body.put("returnUrl", returnUrl);
        body.put("signature", signature);

        // 3. Call PayOS API directly using RestTemplate
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(body, headers);
        
        try {
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                return (String) data.get("checkoutUrl");
            }
        } catch (Exception e) {
            System.err.println("PayOS API Error: " + e.getMessage());
            throw e;
        }
        throw new Exception("Failed to create PayOS payment link");
    }

    public void handlePayOSWebhook(Webhook webhook) {
        try {
            // 1. Verify Webhook Signature
            var data = payOS.verifyPaymentWebhookData(webhook);

            // 2. Extract Order Code
            Long orderCode = data.getOrderCode();

            // 3. Find Order and Complete Payment
            Order order = orderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            // Only complete if not already paid
            orderService.completePayment(order.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA256");
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : rawHmac) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
