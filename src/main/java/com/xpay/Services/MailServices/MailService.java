package com.xpay.Services.MailServices;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    /**
     * Sends an OTP email to the user.
     *
     * @param email      The recipient email address.
     * @param otpString  The generated OTP code.
     */
    public boolean sendOTPEmail(String email, String otpString) {
        if (email == null || email.isEmpty()) {
            log.error("Failed to send OTP: Email address is null or empty.");
            return false;
        }

        try {
            // Create a new MIME message for sending the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Email subject
            helper.setSubject("XPay OTP Verification - Expiry in 2 Minutes");

            // HTML content for the OTP message
            String messageBody = "<html><body>" +
                    "<h3>Dear User,</h3>" +
                    "<p>Your OTP for XPay verification is: <strong>" + otpString + "</strong></p>" +
                    "<p>This OTP is valid for the next 2 minutes. Please do not share it with anyone.</p>" +
                    "<p>Regards,<br/>The XPay Team</p>" +
                    "</body></html>";

            // Set the message body with HTML enabled
            helper.setText(messageBody, true);

            // Set the 'from' address for the email
            helper.setFrom("devloperindia03@gmail.com");

            // Set the recipient email address
            helper.setTo(email);

            // Send the email using the mail sender
            mailSender.send(message);
            log.info("OTP sent successfully to: {}", email);
            return true;
        } catch (Exception e) {
            // Log detailed error information
            log.error("Failed to send OTP to {}: {}", email, e.getMessage());
            return false;
        }
    }
}
