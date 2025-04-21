package com.xpay.Entitys.Client.ClientDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ClientLoginDTO {
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String password;
}
