package com.xpay.Entitys.Transcations;

import com.xpay.Entitys.User.User;
import com.xpay.Entitys.User.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String paymentId;

    private Double amount;
    private String transactionType;
    private String transactionId;
    private String transactionMethod;
    private String status;

    @ManyToOne
    @JoinColumn(name = "wallet_mobile", referencedColumnName = "mobile", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user;

    private LocalDateTime timestamp;
    @PrePersist
    private void generatePaymentId() {
        this.paymentId = generateReadablePaymentId();
        this.timestamp = LocalDateTime.now();
    }

    private String generateReadablePaymentId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // Letters + Numbers
        SecureRandom random = new SecureRandom();
        StringBuilder paymentId = new StringBuilder("TXN-");

        // Generate a 10-character alphanumeric string (including the "TXN-" prefix)
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            paymentId.append(characters.charAt(index));
        }
        return paymentId.toString();
    }
}
