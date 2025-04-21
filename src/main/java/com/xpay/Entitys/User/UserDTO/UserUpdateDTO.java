package com.xpay.Entitys.User.UserDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserUpdateDTO implements Serializable {
    private String name;
    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;
    private  String profileUrl;
    private Integer age;
}
