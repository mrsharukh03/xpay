package com.xpay.Entitys.Admin;

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
@AllArgsConstructor @NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Business Name is required and cannot be blank")
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    private boolean active;

    private boolean emailVerified;

    public Admin(String name, String email, String mobile, String password) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
    }
}
