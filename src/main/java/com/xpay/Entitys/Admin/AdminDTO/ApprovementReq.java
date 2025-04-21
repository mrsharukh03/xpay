package com.xpay.Entitys.Admin.AdminDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApprovementReq {
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    boolean approve;
}
