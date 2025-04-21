package com.xpay.Controller;

import com.xpay.Anotations.RateLimit;
import com.xpay.Entitys.Client.ClientDTO.ClientLoginDTO;
import com.xpay.Entitys.Client.ClientDTO.ClientRegDTO;
import com.xpay.Entitys.Client.ClientDTO.ClientTransactionDTO;
import com.xpay.Entitys.Client.ClientDTO.ForgetPasseordRequest;
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
@RequestMapping("/api/v1/client")
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

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<String> otpVerification(@RequestParam String otp,String email){
        return clintServices.verifyOTP(email,otp);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody ClientLoginDTO loginDTO){
        return clintServices.authenticate(loginDTO);
    }

    @PostMapping("/auth/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email){
        if(email == null) return new ResponseEntity<>("Email Cant be null ",HttpStatus.BAD_REQUEST);
       return clintServices.forgetPassword(email);
    }

    @PatchMapping("/auth/new-password")
    public ResponseEntity<?> newPassword(@Valid @RequestBody ForgetPasseordRequest forgetPasseordRequest){
        return clintServices.newPassword(forgetPasseordRequest);
    }


    // This function help us Client Side KYC Process account Activation.
    @PatchMapping("/verifyUser")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> verifyUser(@RequestParam String userMobile,Boolean status){
        return clintServices.verifyUser(userMobile,status);
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

    @PatchMapping("refund-money")
    public ResponseEntity<?> refundMoney(@RequestParam String paymentId, @AuthenticationPrincipal UserDetails userDetails){
         return clintServices.refund(paymentId,userDetails.getUsername());
    }


}
