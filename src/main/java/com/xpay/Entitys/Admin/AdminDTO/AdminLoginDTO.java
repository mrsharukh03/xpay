package com.xpay.Entitys.Admin.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class AdminLoginDTO {
    String email;
    String password;
}
