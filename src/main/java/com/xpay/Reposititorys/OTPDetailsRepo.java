package com.xpay.Reposititorys;

import com.xpay.Entitys.Client.OTPDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPDetailsRepo extends JpaRepository<OTPDetails,String> {
}
