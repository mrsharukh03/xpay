package com.xpay.Controller;

import com.xpay.Anotations.RateLimit;
import com.xpay.Security.AllowOrigins.AllowedOriginsCache;
import com.xpay.Entitys.Admin.AdminDTO.AdminLoginDTO;
import com.xpay.Entitys.Admin.AdminDTO.AdminProfile;
import com.xpay.Entitys.Admin.AdminDTO.AdminRegDTO;
import com.xpay.Entitys.Admin.AdminDTO.ApprovementReq;
import com.xpay.Entitys.Client.ClientDTO.ClientProfileDTO;
import com.xpay.Entitys.User.UserDTO.UserUpdateDTO;
import com.xpay.Services.AdminServices.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Operations", description = "Operations related to Admin Signup,Login ")
public class AdminController {

    private final AdminService adminService;
    @Autowired
    private AllowedOriginsCache allowedOriginsCache;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AdminRegDTO adminRegDTO){
        return adminService.addAdmin(adminRegDTO);
    }

    @PostMapping("/auth/otp-verify")
    public ResponseEntity<?> verifyOTP(@RequestParam String email,String otp){
        return adminService.verifyOTP(email,otp);
    }

    @PostMapping("/auth/resendOTP")
    public ResponseEntity<?> resendOTP(@RequestParam String email){
       return adminService.resandOTP(email);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO loginDTO){
        return adminService.login(loginDTO);
    }

    @PostMapping("/auth/newPassword")
    public ResponseEntity<?> newPassword(@RequestParam String email,String newPassword,String otp){
        return adminService.newPassword(email,newPassword,otp);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<List<ClientProfileDTO>> pendingClient(){
        return adminService.pendingClients();
    }

    @PatchMapping("/approve-client")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 1, window = 60)
    public ResponseEntity<String> approveClint(@Valid @RequestBody ApprovementReq approveReq){
        ResponseEntity<String> response = adminService.approveClient(approveReq);
        if(response.getStatusCode().equals(200)) allowedOriginsCache.refreshAllowedOrigins();
        return response;
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> allclients(){
        List<ClientProfileDTO> allClints = adminService.getAllClients();
        if(allClints.isEmpty()) return new ResponseEntity<>("No not Found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(allClints,HttpStatus.OK);
    }

    @PatchMapping("/blockClient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 1, window = 60)
    public ResponseEntity<?> blockClient(@RequestParam String email){
        return adminService.blockClient(email);
    }

    @GetMapping("/admins")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> getAdmins(){
        List<AdminProfile> allAdmins = adminService.getAllAdmins();
        if(allAdmins.isEmpty()) return new ResponseEntity<>("No not Found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(allAdmins,HttpStatus.OK);
    }

    @PatchMapping("/approve-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 1, window = 60*10)
    public ResponseEntity<String> approveAdmin(@Valid @RequestBody ApprovementReq approveReq){
        return adminService.approveAdmin(approveReq);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> users(){
        List<UserUpdateDTO> allUsers =  adminService.getAllUsers();
        if(allUsers.isEmpty()) return new ResponseEntity<>("Users not found",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(allUsers,HttpStatus.OK);
    }


}
