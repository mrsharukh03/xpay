package com.xpay.Reposititorys;

import com.xpay.Entitys.Admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminRepo extends JpaRepository<Admin, UUID> {

    boolean existsByEmail(String email);

    Admin findByEmail(String email);
}
