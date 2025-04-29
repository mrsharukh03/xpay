package com.xpay.Services.ClientServices;

import com.xpay.Security.JWT.JWTUtils;
import com.xpay.Entitys.Client.CGSTransactions;
import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.Client.ClientAc;
import com.xpay.Entitys.Client.ClientDTO.*;
import com.xpay.Entitys.Transcations.Transaction;
import com.xpay.Entitys.User.User;
import com.xpay.Entitys.User.Wallet;
import com.xpay.Reposititorys.*;
import com.xpay.Services.OTPServices.OTPServices;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClintServices {

    private final ClientRepo clientRepo;
    private final WalletRepo walletRepo;
    private final ClientACRepo clientACRepo;
    private final TransactionRepo transactionRepo;
    private final CGSTransactionsRepo cgsTransactionsRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ClintServices(ClientRepo clientRepo,WalletRepo walletRepo, ClientACRepo clientACRepo, TransactionRepo transactionRepo, CGSTransactionsRepo cgsTransactionsRepo) {
        this.clientRepo = clientRepo;
        this.walletRepo = walletRepo;
        this.clientACRepo = clientACRepo;
        this.transactionRepo = transactionRepo;
        this.cgsTransactionsRepo = cgsTransactionsRepo;
    }

    // Constants for error messages
    private static final String CLIENT_ALREADY_EXISTS = "Client with this email already exists.";
    private static final String REGISTRATION_FAILED = "Something went wrong or Invalid data.";
    private static final String SERVER_FAILED = "Something went wrong!";
    private static final String CLIENT_NOT_FOUND = "Client not found!";

    public ResponseEntity<String> register(ClientRegDTO regDTO){
        Client existingClient = clientRepo.findByEmail(regDTO.getEmail());
        if(existingClient != null){
            if(existingClient.isEmailVerified()) return new ResponseEntity<>(CLIENT_ALREADY_EXISTS,HttpStatus.ALREADY_REPORTED);
            if(!OTPServices.alreadyActiveOTP(regDTO.getEmail())){
                Map<String,String> sentNewOTP = OTPServices.sendOTP(regDTO.getEmail());
                if(sentNewOTP.get("status").equals("true")) return new ResponseEntity<>("New OTP Sent Successfully",HttpStatus.OK);
                return new ResponseEntity<>(sentNewOTP.get("msg"),HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>("An OTP already Sent to your email",HttpStatus.ALREADY_REPORTED);
            }
        }
        Client client = new Client();
        client.setBusinessName(regDTO.getBusinessName());
        client.setEmail(regDTO.getEmail());
        client.setMobile(regDTO.getMobile());
        client.setWebsiteUrl(regDTO.getWebsiteUrl());
        client.setPassword(passwordEncoder.encode(regDTO.getPassword()));
        client.setApproved(false);
        try {
            clientRepo.save(client);
            OTPServices.sendOTP(regDTO.getEmail());
        } catch (Exception e) {
            log.error("Error saving client: {}", e.getMessage());  // Log the error
            return new ResponseEntity<>(REGISTRATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("An OTP has bess sent to your email ", HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> authenticate(ClientLoginDTO clientLoginDTO) {
        try{
            Client client = clientRepo.findByEmail(clientLoginDTO.getEmail());
            if (client == null) {
                return new ResponseEntity<>(Collections.singletonMap("error", "Email not found"), HttpStatus.NOT_FOUND);
            }
            if (passwordEncoder.matches(clientLoginDTO.getPassword(), client.getPassword())) {
                if (!client.isApproved()) {
                    return new ResponseEntity<>(Collections.singletonMap("error", "Account blocked by admin"), HttpStatus.NOT_ACCEPTABLE);
                }

                Map<String, String> response = new HashMap<>();
                response.put("token", jwtUtils.generateToken(clientLoginDTO.getEmail(), "CLIENT"));
                response.put("apiKey", client.getApiKey());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("Error Client Authentication!");
            return new ResponseEntity<>(Collections.singletonMap("error", SERVER_FAILED),HttpStatus.INTERNAL_SERVER_ERROR);
        }
            return new ResponseEntity<>(Collections.singletonMap("error", "Invalid password"), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<String> verifyUser(String userMobile, Boolean status) {
        try{
            Wallet wallet = walletRepo.findById(userMobile).orElse(null);
            if (wallet != null) {
                wallet.setActivation(status);
                walletRepo.save(wallet);
                return new ResponseEntity<>("Wallet verification status updated successfully", HttpStatus.OK);
            } else{
        return new ResponseEntity<>("Wallet not found", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            log.error("Error When Verifying User Wallet {} ",e.getMessage());
          return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Cacheable(value = "clientTransactions",key = "#username")
    public List<ClientTransactionDTO> clientsTransactions(String username) {
        try{
            Client client = clientRepo.findByEmail(username);
            if(client != null && client.getMobile() !=null){
                List<CGSTransactions> transactions = cgsTransactionsRepo.findByClient(client);
                if(transactions.isEmpty()) return new ArrayList<>();
                List<ClientTransactionDTO> allTransactions = transactions.stream()
                        .map(transaction -> modelMapper.map(transaction, ClientTransactionDTO.class))
                        .collect(Collectors.toList());
                return allTransactions;
            }else{
                throw new IllegalAccessException(CLIENT_NOT_FOUND);
            }
        }catch (Exception e){
            log.error("error fetching Client Transactions {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public ResponseEntity<String> verifyOTP(String email ,String otp) {
        try{
            Client client = clientRepo.findByEmail(email);
            if(client != null){
                Map<String,String> validateOTP = OTPServices.verifyOTP(email,otp);
                if(validateOTP.get("status").equals("true")){
                    client.setEmailVerified(true);
                    clientRepo.save(client);
                    return new ResponseEntity<>("OTP verified Success",HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(validateOTP.get("msg"),HttpStatus.BAD_REQUEST);
                }
            }else {
                return new ResponseEntity<>("Email not found",HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
          log.error("Error verifying OTP {}",e.getMessage());
          return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> refund(String paymentId, String username) {
        try {
            Client client = clientRepo.findByEmail(username);
            if(client != null) {
                CGSTransactions transactions = cgsTransactionsRepo.findById(paymentId).orElse(null);
                if(transactions != null){
                    if(transactions.getClient().equals(client)){
                        User user = transactions.getUser();
                        ClientAc clientAc = clientACRepo.findByClient(client);
                        Wallet userWallet = walletRepo.findById(user.getMobile()).orElse(null);
                        if(userWallet != null && clientAc !=null){
                            userWallet.setBalance(userWallet.getBalance()+transactions.getAmount());
                            clientAc.setBalance(clientAc.getBalance()-transactions.getAmount());
                            Transaction newTransaction = new Transaction();
                            newTransaction.setAmount(transactions.getAmount());
                            newTransaction.setUser(user);
                            newTransaction.setWallet(userWallet);
                            newTransaction.setTransactionType("Refund");
                            newTransaction.setTransactionMethod("System");
                            newTransaction.setStatus("success");
                            walletRepo.save(userWallet);
                            clientACRepo.save(clientAc);
                            transactionRepo.save(newTransaction);
                            return new ResponseEntity<>("Payment Refund Successfully",HttpStatus.OK);
                        }else {
                            return new ResponseEntity<>("Wallet or Account not Found",HttpStatus.NOT_FOUND);
                        }
                    }else{
                        return new ResponseEntity<>("Invalid Credential",HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Transaction not found",HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>(CLIENT_NOT_FOUND,HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
        log.error("Error during Refund {}",e.getMessage());
        return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> forgetPassword(String email) {
        try{
            Client client = clientRepo.findByEmail(email);
            Map<String,String> response = new HashMap<>();
            if(client != null){
                response = OTPServices.sendOTP(email);
                if(response.get("status").equals("true"))
                    return new ResponseEntity<>(response.get("msg"),HttpStatus.OK);
            }
            if(response.containsKey("msg")){
                return new ResponseEntity<>(response.get("msg"),HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
           log.error("Error Clint Forget Password!");
        }
        return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
   }


    public ResponseEntity<?> newPassword(ForgetPasseordRequest forgetPasseordRequest) {
        if(forgetPasseordRequest.getEmail() == null || forgetPasseordRequest.getNewPassword() == null || forgetPasseordRequest.getOtp() == null){
            return new ResponseEntity<>("Invalid Arguments",HttpStatus.BAD_REQUEST);
        }
        try{
            Client client = clientRepo.findByEmail(forgetPasseordRequest.getEmail());
            if(client != null){
                Map<String,String> isVarified =  OTPServices.verifyOTP(forgetPasseordRequest.getEmail(),forgetPasseordRequest.getOtp());
                if(isVarified.get("status").equals("true")){
                    client.setPassword(passwordEncoder.encode(forgetPasseordRequest.getNewPassword()));
                    clientRepo.save(client);
                    return new ResponseEntity<>("Password Changed Successfully",HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(isVarified.get("msg"),HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Email not found ",HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            log.error("Error otp client new password creation");
            return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getById(String username) {
        try{
           Client client = clientRepo.findByEmail(username);
           if(client != null){
              ClientProfileDTO response = modelMapper.map(client,ClientProfileDTO.class);
              return new ResponseEntity<>(response,HttpStatus.OK);
           }
        }catch (Exception e){
            log.error("Error fetching client by email {}",e.getMessage());
            return new ResponseEntity<>("Something went wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CLIENT_NOT_FOUND,HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateProfile(ClientProfileUpdate updateDTO, String username) {
        try{
            Client client = clientRepo.findByEmail(username);
            if(client != null){
                if(updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()){
                    if(updateDTO.getEmail().equals(username) || clientRepo.existsByEmail(updateDTO.getEmail())) return new ResponseEntity<>("Email Already exist!",HttpStatus.BAD_REQUEST);
                    client.setEmail(updateDTO.getEmail());
                    client.setEmailVerified(false);
                }
                if(updateDTO.getBusinessName() != null && !updateDTO.getBusinessName().isEmpty()) client.setBusinessName(updateDTO.getBusinessName());
                if(updateDTO.getMobile() != null && !updateDTO.getMobile().isEmpty()) client.setMobile(updateDTO.getMobile());
                if(updateDTO.getWebsiteUrl() != null && !updateDTO.getWebsiteUrl().isEmpty()) client.setWebsiteUrl(updateDTO.getWebsiteUrl());

                clientRepo.save(client);
                return new ResponseEntity<>("Profile Successfully Updated",HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("Error Updating Client Profile {}",e.getMessage());
            return new ResponseEntity<>(SERVER_FAILED,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CLIENT_NOT_FOUND,HttpStatus.NOT_FOUND);
    }
}
