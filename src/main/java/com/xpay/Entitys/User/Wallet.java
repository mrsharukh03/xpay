package com.xpay.Entitys.User;

import com.xpay.Entitys.Client.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    private Double balance = 0.0;
    private boolean activation = false;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "email", nullable = false)
    private Client client;
}
