package com.xpay.Entitys.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPDetails {

    @Id
    private String email;
    private String otp;
    private LocalDateTime generateTime;
    private LocalDateTime expirationTime;
        private int requestCount;
        private LocalDateTime lastRequestTime;


    @PrePersist
    private void countAssign(){
        requestCount =0;
        lastRequestTime = LocalDateTime.now();
    }

    public OTPDetails(String email, String otpString, LocalDateTime now, LocalDateTime expirationTime) {
        this.email = email;
        otp = otpString;
        generateTime =now;
        this.expirationTime =expirationTime;
    }
}

