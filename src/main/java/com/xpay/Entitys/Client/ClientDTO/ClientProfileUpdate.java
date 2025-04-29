package com.xpay.Entitys.Client.ClientDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ClientProfileUpdate {

    private String businessName;
    @Email(message = "Please provide a valid email address")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;
    private String websiteUrl;
}
