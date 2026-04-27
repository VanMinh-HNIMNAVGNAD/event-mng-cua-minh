package com.sa.event_mng.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
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
            System.out.println("✅ Đã gửi email xác thực thành công tới: " + to);
        } catch (Exception e) {
            System.err.println("⚠️ KHÔNG THỂ GỬI EMAIL XÁC THỰC! (Do chưa cấu hình SMTP).");
            System.err.println("👉 LINK XÁC THỰC CỦA BẠN LÀ: " + verificationUrl);
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
            System.out.println("✅ Đã gửi email OTP thành công tới: " + to);
        } catch (Exception e) {
            System.err.println("⚠️ KHÔNG THỂ GỬI EMAIL OTP! (Do chưa cấu hình SMTP).");
            System.err.println("👉 MÃ OTP CỦA BẠN LÀ: " + otp);
        }
    }

    public void sendOrderConfirmationWithInvoice(String to, com.sa.event_mng.model.entity.Order order, byte[] pdfBytes) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);

            String subject = "[Event Manager] Xác nhận thanh toán & Hóa đơn điện tử - Order #" + order.getId();
            String content = "Chào bạn,\n\n" +
                    "Chúng tôi xác nhận đơn hàng " + order.getId() + " đã được thanh toán thành công.\n" +
                    "Tổng số tiền: " + String.format("%,.0f", order.getTotalAmount().doubleValue()) + "đ\n\n" +
                    "Vui lòng xem hóa đơn điện tử (E-Invoice) được đính kèm trong email này.\n\n" +
                    "Trân trọng!\n\n" +
                    "--------------------------------------------------\n" +
                    "Dear customer,\n\n" +
                    "Your payment for order " + order.getId() + " was successful.\n" +
                    "Total amount: " + String.format("%,.0f", order.getTotalAmount().doubleValue()) + " VND\n\n" +
                    "Please find your attached E-Invoice in this email.\n\n" +
                    "Best regards!";

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            
            // Attach PDF
            helper.addAttachment("Invoice_" + order.getId() + ".pdf", new org.springframework.core.io.ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
