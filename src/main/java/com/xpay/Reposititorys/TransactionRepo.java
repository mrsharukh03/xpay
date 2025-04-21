package com.xpay.Reposititorys;

import com.xpay.Entitys.Transcations.Transaction;
import com.xpay.Entitys.User.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByWallet(Wallet wallet);
}
