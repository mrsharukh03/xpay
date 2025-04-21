package com.xpay.Security.UserDetails;

import com.xpay.Entitys.Admin.Admin;
import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.User.User;
import com.xpay.Reposititorys.AdminRepo;
import com.xpay.Reposititorys.ClientRepo;
import com.xpay.Reposititorys.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final ClientRepo clientRepo;
    private final AdminRepo adminRepo;
    private final UserRepo userRepo;
    @Autowired
    public CustomUserDetailsService(ClientRepo clientRepo, AdminRepo adminRepo, UserRepo userRepo) {
        this.clientRepo = clientRepo;
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(isAdminLogin(email)){
            Admin admin = adminRepo.findByEmail(email);
            if (admin != null) {
                return new CustomUserDetails(admin.getEmail(),admin.getPassword(),"ADMIN");
            }
        }
        Client client = clientRepo.findByEmail(email);
        if (client != null) {
            return new CustomUserDetails(client.getEmail(),client.getPassword(),"CLIENT");
        }

        User user = userRepo.findById(email).orElse(null);
        if(user !=null){
            return new CustomUserDetails(user.getEmail(),user.getPassword(),"USER");
        }
        throw new UsernameNotFoundException("User not found");
    }
    private boolean isAdminLogin(String email) {
        return email.endsWith("@xpay.com") || email.equals("devloperindia03@gmail.com");
    }
}
