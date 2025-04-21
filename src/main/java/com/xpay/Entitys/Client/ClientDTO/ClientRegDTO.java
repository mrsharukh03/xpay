package com.xpay.Entitys.Client.ClientDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegDTO {
    @Column(nullable = false)
    private String businessName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String mobile;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String websiteUrl;
}
