package com.xpay.Reposititorys;

import com.xpay.Entitys.Client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientRepo extends JpaRepository<Client, UUID> {

    List<Client> findByIsApprovedFalse();

    List<Client> findByIsApprovedTrue();

    Client findByEmail(String email);

    boolean existsByEmail(String email);

    Client findByApiKey(String apiKey);
}
