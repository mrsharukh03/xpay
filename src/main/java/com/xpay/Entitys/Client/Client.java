package com.xpay.Entitys.Client;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String businessName;

    @Column(unique = true, nullable = false)
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(unique = true,nullable = false)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;


    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Column(unique = true)
    private String websiteUrl;

    private boolean isApproved;
    private boolean emailVerified;

    @Column(unique = true, nullable = false)
    private String apiKey;

    @PrePersist
    public void generateApiKey() {
        this.apiKey = UUID.randomUUID().toString();
    }
}
