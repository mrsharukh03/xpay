package com.xpay.Services.ClientServices;

import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.Client.ClientAc;
import com.xpay.Reposititorys.ClientACRepo;
import com.xpay.Reposititorys.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.nio.file.AccessDeniedException;

@Service
public class ClientACService {

    private final ClientRepo clientRepo;
    private final ClientACRepo clientACRepo;
    @Autowired
    public ClientACService(ClientRepo clientRepo, ClientACRepo clientACRepo) {
        this.clientRepo = clientRepo;
        this.clientACRepo = clientACRepo;
    }


    @Cacheable(value = "clientBalance", key = "#username")
    public Double getBalance(String username) throws Exception {
        Client client = clientRepo.findByEmail(username);
        if (client == null) {
            throw new UsernameNotFoundException("Client not found");
        }

        if (!client.isApproved()) {
            throw new AccessDeniedException("This account is blocked for some reason");
        }

        ClientAc clientAc = clientACRepo.findById(client.getMobile()).orElse(null);
        if (clientAc == null) {
            throw new AccountNotFoundException("Account not found");
        }
        return clientAc.getBalance();
    }
}
