package com.xpay.Entitys.Client.ClientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
 @Data @NoArgsConstructor @AllArgsConstructor
public class ClientTransactionDTO implements Serializable {
     private String paymentId;
    private Double amount;
    private String productCategory;
    private String productName;
    private LocalDateTime transactionTime;
    private String status;
}
