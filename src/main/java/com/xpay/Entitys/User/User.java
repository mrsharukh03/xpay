package com.xpay.Entitys.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private String profileUrl;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    private Integer age;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @CreationTimestamp
    @NotNull(message = "CreatedAt is required")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull(message = "UpdatedAt is required")
    private LocalDateTime updatedAt;
}
