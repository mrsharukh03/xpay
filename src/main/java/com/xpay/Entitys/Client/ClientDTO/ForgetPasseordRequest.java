package com.xpay.Entitys.Client.ClientDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgetPasseordRequest {
    @NotNull
    String otp;
    @NotNull
    @Email
    String email;
    @NotNull
    String newPassword;
}
