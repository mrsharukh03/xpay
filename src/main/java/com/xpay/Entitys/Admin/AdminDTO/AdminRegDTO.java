package com.xpay.Entitys.Admin.AdminDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminRegDTO {
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String mobile;
    @Column(nullable = false)
    String password;
}
