package com.xpay.Entitys.Admin.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminProfile implements Serializable {
    String name;
    String email;
    String mobile;
    boolean active;
}
