package com.sa.event_mng.shared.infrastructure.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final org.thymeleaf.TemplateEngine templateEngine;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.mail.password}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.backend.url}")
    private String backendUrl;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = backendUrl + "/auth/verify?token=" + token;
        String htmlContent = "<h3>Xác thực Email</h3>" +
                "<p>Vui lòng nhấn vào liên kết bên dưới để xác thực email của bạn:</p>" +
                "<a href='" + verificationUrl + "'>" + verificationUrl + "</a>";

        sendEmailViaApi(to, "[Event Manager] Xác thực Email", htmlContent, null, null);
    }

    public void sendOtpEmail(String to, String otp) {
        String htmlContent = "<h3>Mã xác thực OTP</h3>" +
                "<p>Mã OTP để đặt lại mật khẩu của bạn là: <b>" + otp + "</b></p>" +
                "<p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>";

        sendEmailViaApi(to, "[Event Manager] Mã xác thực OTP", htmlContent, null, null);
    }

    public void sendOrderConfirmationWithInvoice(String to, com.sa.event_mng.modules.ordering.domain.model.Order order, byte[] pdfBytes) {
        try {
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("orderId", order.getId());
            context.setVariable("totalAmount", String.format("%,.0f", order.getTotalAmount().doubleValue()));
            context.setVariable("orderDate", order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("tickets", order.getTickets());

            String htmlContent = templateEngine.process("order-confirmation", context);
            String base64Content = Base64.getEncoder().encodeToString(pdfBytes);
            
            sendEmailViaApi(to, "[Event Hub] Xác nhận đơn hàng #" + order.getId(), htmlContent, "Invoice_" + order.getId() + ".pdf", base64Content);
        } catch (Exception e) {
            log.error("Lỗi chuẩn bị email xác nhận: {}", e.getMessage());
        }
    }

    private void sendEmailViaApi(String to, String subject, String htmlContent, String fileName, String base64Content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "Event Hub", "email", fromEmail));
            body.put("to", List.of(Map.of("email", to)));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            if (fileName != null && base64Content != null) {
                body.put("attachments", List.of(Map.of(
                        "name", fileName,
                        "content", base64Content
                )));
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Đã gửi email thành công tới: {}", to);
            } else {
                log.error("Lỗi gửi email qua API Brevo: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("KHÔNG THỂ GỬI EMAIL QUA API! Lỗi: {}", e.getMessage());
        }
    }
}
