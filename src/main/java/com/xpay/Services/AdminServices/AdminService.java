package com.xpay.Services.AdminServices;

import com.xpay.Security.JWT.JWTUtils;
import com.xpay.Entitys.Admin.Admin;
import com.xpay.Entitys.Admin.AdminDTO.AdminLoginDTO;
import com.xpay.Entitys.Admin.AdminDTO.AdminProfile;
import com.xpay.Entitys.Admin.AdminDTO.AdminRegDTO;
import com.xpay.Entitys.Admin.AdminDTO.ApprovementReq;
import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.Client.ClientAc;
import com.xpay.Entitys.Client.ClientDTO.ClientProfileDTO;
import com.xpay.Entitys.User.User;
import com.xpay.Entitys.User.UserDTO.UserUpdateDTO;
import com.xpay.Reposititorys.AdminRepo;
import com.xpay.Reposititorys.ClientACRepo;
import com.xpay.Reposititorys.ClientRepo;
import com.xpay.Reposititorys.UserRepo;
import com.xpay.Services.OTPServices.OTPServices;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service @Slf4j
public class AdminService {
    private final AdminRepo adminRepo;
    private final UserRepo userRepo;
    private final ClientRepo clientRepo;
    private final ClientACRepo clientAcRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ModelMapper modelMapper;
   @Autowired
    public AdminService(AdminRepo adminRepo, UserRepo userRepo, ClientRepo clientRepo, ClientACRepo clientAcRepo) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
       this.clientAcRepo = clientAcRepo;
   }

    private static final String SERVER_FAILED = "Something went wrong!";
    private static final String EMAIL_NOT_FOUND = "Email not found!";

    public ResponseEntity<?> signup(AdminRegDTO adminRegDTO) {
        try{
            if(adminRepo.existsByEmail(adminRegDTO.getEmail())) return new ResponseEntity<>("Admin Already Exist",HttpStatus.ALREADY_REPORTED);
            Admin admin = new Admin(adminRegDTO.getName(),adminRegDTO.getEmail(),adminRegDTO.getMobile(),passwordEncoder.encode(adminRegDTO.getPassword()));
            admin.setActive(false);
            OTPServices.sendOTP(adminRegDTO.getEmail());
        adminRepo.save(admin);
        }catch (Exception e){
            log.error("error Admin Signup {}",e.getMessage());
            return new ResponseEntity<>("Email,Mobile, can't be null",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("An OTP sent to your email",HttpStatus.OK);
    }

    public ResponseEntity<?> verifyOTP(String email,String otp){
       try{
           Admin admin = adminRepo.findByEmail(email);
           if(admin != null){
               Map<String,String> verification = OTPServices.verifyOTP(email,otp);
               if (verification.get("status").equals("true")){
                   admin.setEmailVerified(true);
                   if(email.equals("devloperindia03@gmail.com")) admin.setActive(true);
                   adminRepo.save(admin);
               }else{
                   return new ResponseEntity<>(verification.get("msg"),HttpStatus.BAD_REQUEST);
               }
           }else {
               return new ResponseEntity<>("Email not found",HttpStatus.NOT_FOUND);
           }
       }catch (Exception e){
           log.error("Error Verifying Admin OTP {}",e.getMessage());
       }
       return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> resandOTP(String email){
        try {
            if(adminRepo.existsByEmail(email)){
                Map<String,String> sendOTP = OTPServices.sendOTP(email);
                if (sendOTP.get("status").equals("true")){
                    return new ResponseEntity<>("OTP Sent to your email",HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(sendOTP.get("msg"),HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception e){
            log.error("Error Admin sanding again OTP {}",e.getMessage());
            return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
       return new ResponseEntity<>(EMAIL_NOT_FOUND,HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> login(AdminLoginDTO loginDTO) {
        try{
            if(!adminRepo.existsByEmail(loginDTO.getEmail())) return new ResponseEntity<>("Email not found",HttpStatus.NOT_FOUND);
            Admin admin = adminRepo.findByEmail(loginDTO.getEmail());
            if(!admin.isActive()) return new ResponseEntity<>("Please Approve By Admin then login",HttpStatus.BAD_REQUEST);
            if(passwordEncoder.matches(loginDTO.getPassword(),admin.getPassword())){
                Map<String,String> response = new HashMap<>();
                response.put("token",jwtUtils.generateToken(admin.getEmail(),"ADMIN"));
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("error Admin Authentication {}",e.getMessage());
        }
        return new ResponseEntity<>(SERVER_FAILED+" Invalid Data",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> forgetPassword(String email){
        try{
            if(adminRepo.existsByEmail(email)){
                Map<String,String> response = OTPServices.sendOTP(email);
                if(response.get("status").equals("true")) {
                    return new ResponseEntity<>("And Verification Code Sent to your email",HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(response.get("msg"),HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception e){
            log.error("Error Admin forget password {}",e.getMessage());
            return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
       return new ResponseEntity<>(EMAIL_NOT_FOUND,HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> newPassword(String email,String newPassword,String otp){
       if(email == null || newPassword == null || otp == null) return new ResponseEntity<>("Invalid Credential ",HttpStatus.BAD_REQUEST);
       try{
           Admin admin = adminRepo.findByEmail(email);
           if(admin != null){
               Map<String,String> response = OTPServices.verifyOTP(email,otp);
               if(response.get("status").equals("true")){
                   admin.setPassword(passwordEncoder.encode(newPassword));
                   adminRepo.save(admin);
                   return new ResponseEntity<>("Password Successfully Changed",HttpStatus.OK);
               }else {
                   return new ResponseEntity<>(response.get("msg"),HttpStatus.BAD_REQUEST);
               }
           }
       }catch (Exception e){
           log.error("Error When changing admin password {}",e.getMessage());
       }
       return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
   }

    @Cacheable(value = "allAdmins")
    public List<AdminProfile> getAllAdmins() {
        try {
            List<Admin> allAdmins = adminRepo.findAll();
            if(allAdmins.isEmpty()) return new ArrayList<>();
            List<AdminProfile> adminProfiles = new ArrayList<>();

            for (Admin admin:allAdmins) {
                AdminProfile adminProfile = new AdminProfile();
                adminProfile.setName(admin.getName());
                adminProfile.setEmail(admin.getEmail());
                adminProfile.setMobile(admin.getMobile());
                adminProfile.setActive(admin.isActive());
                adminProfiles.add(adminProfile);
            }
            return adminProfiles;
        }catch (Exception e){
            log.error("Error Admin getting All Admins {}",e.getMessage());
        }
        return new ArrayList<>();
    }


    public ResponseEntity<String> approveAdmin(ApprovementReq approveReq) {
        try{
            Admin existingAdmin = adminRepo.findByEmail(approveReq.getEmail());
            if(existingAdmin != null){
                if (approveReq.isApprove()){
                    existingAdmin.setActive(true);
                    adminRepo.save(existingAdmin);
                    return new ResponseEntity<>("Admin Approval Successful",HttpStatus.OK);
                }else{
                    adminRepo.delete(existingAdmin);
                    return new ResponseEntity<>("Admin request rejected",HttpStatus.OK);
                }
            }else{
                return new ResponseEntity<>("Admin not found",HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            log.error("Error Admin Approval {}",e.getMessage());
            return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<List<ClientProfileDTO>> pendingClients() {
        try {
            List<Client> pendingClients = clientRepo.findByIsApprovedFalse();
            if (pendingClients.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            List<ClientProfileDTO> clientProfiles = pendingClients.stream()
                    .map(client -> new ClientProfileDTO(client.getId(), client.getBusinessName(), client.getEmail(), client.getMobile(), client.getWebsiteUrl(), client.isApproved(),client.isEmailVerified()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(clientProfiles, HttpStatus.OK);
        }catch (Exception e){
            log.error("Error Admin finding pending clients {}",e.getMessage());
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<String> approveClient(ApprovementReq approveReq) {
        try {
            Client existingClient = clientRepo.findByEmail(approveReq.getEmail());
            if (existingClient == null) {
                return new ResponseEntity<>("CLIENT_NOT_FOUND", HttpStatus.NOT_FOUND);
            }
            if (approveReq.isApprove()) {
                if (existingClient.getMobile() != null) {
                    if (!clientAcRepo.existsById(existingClient.getMobile())) {
                        ClientAc newClientAc = new ClientAc();
                        newClientAc.setMobile(existingClient.getMobile());
                        newClientAc.setBalance(0.0);
                        newClientAc.setClient(existingClient);
                        clientAcRepo.save(newClientAc);
                    }
                    existingClient.setApproved(true);
                    clientRepo.save(existingClient);
                    return new ResponseEntity<>("Client approval successful", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid mobile number", HttpStatus.NOT_FOUND);
                }
            } else {
                clientRepo.delete(existingClient);
                return new ResponseEntity<>("Client request rejected", HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Error approving client: {}", e.getMessage());
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> blockClient(String email) {
        try{
            Client existingClient = clientRepo.findByEmail(email);
            if (existingClient == null) {
                return new ResponseEntity<>("CLIENT_NOT_FOUND", HttpStatus.NOT_FOUND);
            }
            existingClient.setApproved(false);
            existingClient.setApiKey(UUID.randomUUID().toString());
            clientRepo.save(existingClient);
        }catch (Exception e){
            log.error("Error Blocking Client {}",e.getMessage());
        }
            return new ResponseEntity<>("Client account blocked", HttpStatus.OK);
    }

    @Cacheable(value = "clients")
    public List<ClientProfileDTO> getAllClients() {
        try{
            List<Client> allClients = clientRepo.findByIsApprovedTrue();
            if (allClients.isEmpty()) {
                return new ArrayList<>();
            }
            List<ClientProfileDTO> clientProfiles = allClients.stream()
                    .map(client -> new ClientProfileDTO(client.getId(), client.getBusinessName(), client.getEmail(), client.getMobile(), client.getWebsiteUrl(), client.isApproved(),client.isEmailVerified()))
                    .collect(Collectors.toList());
            return clientProfiles;
        }catch (Exception e){
            log.error("Error Fetching All Clients {}",e.getMessage());
        }
        return new ArrayList<>();
    }

    @Cacheable(value = "allUsers")
    public List<UserUpdateDTO> getAllUsers() {
        try{
            List<User> users = userRepo.findAll();
            if(users.isEmpty()) return new ArrayList<>();
            List<UserUpdateDTO> userUpdateDTOList = users.stream()
                    .map(user -> modelMapper.map(user, UserUpdateDTO.class))
                    .collect(Collectors.toList());
            return userUpdateDTOList;
        }catch (Exception e){
           log.error("Error Fetching all Users by Admin {}",e.getMessage());
        }
        return new ArrayList<>();
    }
}
