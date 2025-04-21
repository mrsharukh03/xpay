package com.xpay.Reposititorys;

import com.xpay.Entitys.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    User findByMobile(String mobile);
}
