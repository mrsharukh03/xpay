package com.xpay.Controller;

import com.xpay.Anotations.RateLimit;
import com.xpay.Entitys.Transcations.TransactionDTO.TransactionDTO;
import com.xpay.Services.UserServices.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet/")
@Tag(name = "Wallet Operations", description = "Operations related to Creating Wallet Getting Balance & Transactions")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create-wallet")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 3, window = 60*60)
    public ResponseEntity<?> createWallet(@RequestParam String mobile,
                                          @RequestHeader("x-api-key") String apiKey) {
        return walletService.createWallet(mobile, apiKey);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 4, window = 60)
    public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        try {
        Double balance = walletService.getBalance(userDetails.getUsername());
        if(balance == null) return new ResponseEntity<>("Insufficient Balance",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(balance,HttpStatus.OK);
        }catch (AccountNotFoundException e){
            return new ResponseEntity<>("Account not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 5, window = 60)
    public ResponseEntity<?> addMoney(@RequestParam Double amount,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        return walletService.addMoney(amount, userDetails.getUsername());
    }

    @GetMapping("transaction")
    public ResponseEntity<?> transactionById(@RequestParam String paymentId,@AuthenticationPrincipal UserDetails userDetails){
        try{
           TransactionDTO transaction = walletService.getTransactionById(userDetails.getUsername(),paymentId);
           if(transaction == null) return new ResponseEntity<>("Transaction not fount with PaymentID: "+paymentId,HttpStatus.NOT_FOUND);
           return new ResponseEntity<>(transaction,HttpStatus.OK);
        }catch (UsernameNotFoundException e){
           return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 3, window = 60)
    public ResponseEntity<?> userTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<TransactionDTO> transactions = walletService.getUserTransactions(userDetails.getUsername());
            return new ResponseEntity<>(transactions,HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/payment")
    @PreAuthorize("hasRole('ROLE_USER')")
    @RateLimit(limit = 2, window = 60)
    public ResponseEntity<?> payFor(@RequestParam Double amount,
                                    String productCategory,
                                    String productName,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    @RequestHeader("x-api-key") String apiKey) {
        return walletService.payFor(userDetails.getUsername(), amount, productCategory, productName, apiKey);
    }
}
