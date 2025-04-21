package com.xpay.Reposititorys;

import com.xpay.Entitys.User.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends JpaRepository<Wallet,String>{

}
