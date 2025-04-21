package com.xpay.Reposititorys;

import com.xpay.Entitys.Client.CGSTransactions;
import com.xpay.Entitys.Client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CGSTransactionsRepo extends JpaRepository<CGSTransactions,String> {
    List<CGSTransactions> findByClient(Client client);
}
