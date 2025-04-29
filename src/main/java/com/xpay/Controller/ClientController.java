package com.xpay.Controller;

import com.xpay.Anotations.RateLimit;
import com.xpay.Entitys.Client.ClientDTO.*;
import com.xpay.Services.ClientServices.ClientACService;
import com.xpay.Services.ClientServices.ClintServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Client Operations", description = "Operations related Client")
public class ClientController {

    private final ClintServices clintServices;
    private final ClientACService clientACService;

    @Autowired
    public ClientController(ClintServices clintServices, ClientACService clientACService) {
        this.clintServices = clintServices;
        this.clientACService = clientACService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> registerClint(@Valid @RequestBody ClientRegDTO clientRegDTO){
        return clintServices.register(clientRegDTO);
    }

    @PostMapping("/auth/verify/email")
    public ResponseEntity<String> otpVerification(@RequestParam String otp,String email){
        return clintServices.verifyOTP(email,otp);
    }

    @PostMapping("/auth/otp/resend")
    public ResponseEntity<?> resendOTP(@RequestParam String email){
        if(email == null) return new ResponseEntity<>("Email Cant be null ",HttpStatus.BAD_REQUEST);
        return clintServices.forgetPassword(email);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody ClientLoginDTO loginDTO){
        return clintServices.authenticate(loginDTO);
    }

    @PostMapping("/auth/password/forget")
    public ResponseEntity<?> forgetPassword(@RequestParam String email){
        if(email == null) return new ResponseEntity<>("Email Cant be null ",HttpStatus.BAD_REQUEST);
       return clintServices.forgetPassword(email);
    }

    @PatchMapping("/auth/password/reset")
    public ResponseEntity<?> newPassword(@Valid @RequestBody ForgetPasseordRequest forgetPasseordRequest){
        return clintServices.newPassword(forgetPasseordRequest);
    }


    // This function help us Client Side KYC Process account Activation.
    @PatchMapping("/verify/user")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> verifyUser(@RequestParam String userMobile,Boolean status){
        return clintServices.verifyUser(userMobile,status);
    }

    @GetMapping("/")
    public ResponseEntity<?> clientById(@AuthenticationPrincipal UserDetails userDetails){
        return clintServices.getById(userDetails.getUsername());
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody ClientProfileUpdate updateDTO,@AuthenticationPrincipal UserDetails userDetails){
        return clintServices.updateProfile(updateDTO,userDetails.getUsername());
    }


    @GetMapping("/balance")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> getACBalance(@AuthenticationPrincipal UserDetails userDetails){
        try{
            Double amount = clientACService.getBalance(userDetails.getUsername());
            if(amount == null) return new ResponseEntity<>("Insufficient Balance",HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(amount,HttpStatus.OK);
        }catch (UsernameNotFoundException | AccountNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> clientTransactions(@AuthenticationPrincipal UserDetails userDetails){
        List<ClientTransactionDTO> transactionDTOList = clintServices.clientsTransactions(userDetails.getUsername());
        if(transactionDTOList.isEmpty()) return new ResponseEntity<>("Transaction not found ", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(transactionDTOList,HttpStatus.OK);
    }

    @PatchMapping("money/refund")
    public ResponseEntity<?> refundMoney(@RequestParam String paymentId, @AuthenticationPrincipal UserDetails userDetails){
         return clintServices.refund(paymentId,userDetails.getUsername());
    }


}
