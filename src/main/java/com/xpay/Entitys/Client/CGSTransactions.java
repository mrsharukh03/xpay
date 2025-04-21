package com.xpay.Entitys.Client;

import com.xpay.Entitys.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class CGSTransactions {


    @Id
    private String paymentId;

    private Double amount;
    private String productCategory;
    private String productName;
    private LocalDateTime transactionTime;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "client_email", referencedColumnName = "email", nullable = false)
    private Client client;

    @PrePersist
    private void generatePaymentId() {
        this.paymentId = generateReadablePaymentId();
        this.transactionTime = LocalDateTime.now();
    }

    private String generateReadablePaymentId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // Letters + Numbers
        SecureRandom random = new SecureRandom();
        StringBuilder paymentId = new StringBuilder("CGS-");

        // Generate a 10-character alphanumeric string (including the "CGS-" prefix)
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            paymentId.append(characters.charAt(index));
        }
        return paymentId.toString();
    }

}
