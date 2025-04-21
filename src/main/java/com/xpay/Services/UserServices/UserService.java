package com.xpay.Services.UserServices;

import com.xpay.Entitys.User.User;
import com.xpay.Entitys.User.UserDTO.UserUpdateDTO;
import com.xpay.Reposititorys.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepo userRepo;
    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public ResponseEntity<?> updateUser(String userEmail, UserUpdateDTO userUpdateDTO) {
        User user = userRepo.findById(userEmail).orElse(null);
        if(user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        user.setUpdatedAt(LocalDateTime.now());
        if(userUpdateDTO.getName() !=null) user.setName(userUpdateDTO.getName());
        if(userUpdateDTO.getMobile() != null)user.setMobile(userUpdateDTO.getMobile());
        if(userUpdateDTO.getProfileUrl() !=null) user.setProfileUrl(userUpdateDTO.getProfileUrl());
        if(userUpdateDTO.getAge() != null){
            if(userUpdateDTO.getAge() >= 18 && userUpdateDTO.getAge() <= 80){
            user.setAge(userUpdateDTO.getAge());
            }else{
                return new ResponseEntity<>("Age must be between 18 and 80 ",HttpStatus.BAD_REQUEST);
            }
        }
        try{
            userRepo.save(user);
            return new ResponseEntity<>("Update Successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
