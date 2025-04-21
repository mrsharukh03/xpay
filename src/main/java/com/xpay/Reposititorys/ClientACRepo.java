package com.xpay.Reposititorys;

import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.Client.ClientAc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientACRepo extends JpaRepository<ClientAc,String> {

    ClientAc findByClient(Client existingClient);
}
