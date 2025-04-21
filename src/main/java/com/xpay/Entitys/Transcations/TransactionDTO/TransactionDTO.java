package com.xpay.Entitys.Transcations.TransactionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransactionDTO implements Serializable {

    private String paymentId;
    private Double amount;
    private String transactionType;
    private String transactionMethod;
    private String status;
    private LocalDateTime timestamp;
}
