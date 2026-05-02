package com.sa.event_mng.shared.infrastructure.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final org.thymeleaf.TemplateEngine templateEngine;
    
    @org.springframework.beans.factory.annotation.Value("${app.mail.from}")
    private String fromEmail;

    @org.springframework.beans.factory.annotation.Value("${app.backend.url}")
    private String backendUrl;

    public void sendVerificationEmail(String to, String token) {
        String subject = "[Event Manager] Xác thực Email - Email Verification";
        String verificationUrl = backendUrl + "/auth/verify?token=" + token;
        String message = "Vui lòng nhấn vào liên kết bên dưới để xác thực email của bạn:\n" + verificationUrl + "\n\n" +
                         "--------------------------------------------------\n" +
                         "Please click the link below to verify your email:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
            System.out.println("Đã gửi email xác thực thành công tới: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("KHÔNG THỂ GỬI EMAIL XÁC THỰC! (Lỗi: " + e.getMessage() + ")");
            System.err.println("LINK XÁC THỰC CỦA BẠN LÀ: " + verificationUrl);
        }
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "[Event Manager] Mã xác thực OTP - OTP Verification Code";
        String message = "Mã OTP để đặt lại mật khẩu của bạn là: " + otp + "\n" +
                         "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n" +
                         "--------------------------------------------------\n" +
                         "Your OTP code for password reset is: " + otp + "\n" +
                         "If you did not request this, please ignore this email.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
            System.out.println("Đã gửi email OTP thành công tới: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("KHÔNG THỂ GỬI EMAIL OTP! (Lỗi: " + e.getMessage() + ")");
            System.err.println("MÃ OTP CỦA BẠN LÀ: " + otp);
        }
    }

    public void sendOrderConfirmationWithInvoice(String to, com.sa.event_mng.modules.ordering.domain.model.Order order, byte[] pdfBytes) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

            // Prepare Thymeleaf context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("orderId", order.getId());
            context.setVariable("totalAmount", String.format("%,.0f", order.getTotalAmount().doubleValue()));
            context.setVariable("orderDate", order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("tickets", order.getTickets());

            // Process template
            String htmlContent = templateEngine.process("order-confirmation", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[Event Hub] Xác nhận đơn hàng & Vé điện tử - Order #" + order.getId());
            helper.setText(htmlContent, true);
            
            helper.addAttachment("Invoice_" + order.getId() + ".pdf", new org.springframework.core.io.ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Đã gửi email xác nhận thanh toán thành công tới: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }
}
