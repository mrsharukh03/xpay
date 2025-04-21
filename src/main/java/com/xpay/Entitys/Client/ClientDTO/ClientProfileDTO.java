package com.xpay.Entitys.Client.ClientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileDTO implements Serializable {
    private UUID id;
    private String businessName;
    private String email;
    private String mobile;
    private String websiteUrl;
    private boolean isApproved;
    private boolean emailVerified;
}
