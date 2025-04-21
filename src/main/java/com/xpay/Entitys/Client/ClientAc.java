package com.xpay.Entitys.Client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class ClientAc {
    @Id
    private String mobile;
    private Double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "email", nullable = false)
    private Client client;
}
