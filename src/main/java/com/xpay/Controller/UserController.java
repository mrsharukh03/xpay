package com.xpay.Controller;

import com.xpay.Anotations.RateLimit;
import com.xpay.Entitys.User.UserDTO.UserUpdateDTO;
import com.xpay.Services.UserServices.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Operations", description = "Operations related Users")
public class UserController {

   private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<?> googleLogin() {
        // Redirect to Google login page (this can be handled by Spring Security automatically)
        return ResponseEntity.ok("<a href=\"http://192.168.0.169:8080/login/oauth2/authorization/google\">" +
                "    <button>Login with Google</button>\n" +
                "</a>\n");
    }

    @PatchMapping("/updateProfile")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 1, window = 60*60*24)
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userProfileDTO , @AuthenticationPrincipal UserDetails userDetails){
        return userService.updateUser(userDetails.getUsername(),userProfileDTO);
    }

}
