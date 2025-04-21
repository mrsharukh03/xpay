package com.xpay.Services.UserServices;

import com.xpay.Entitys.Client.Client;
import com.xpay.Entitys.Client.ClientAc;
import com.xpay.Entitys.Client.CGSTransactions;
import com.xpay.Entitys.Transcations.Transaction;
import com.xpay.Entitys.Transcations.TransactionDTO.TransactionDTO;
import com.xpay.Entitys.User.User;
import com.xpay.Entitys.User.Wallet;
import com.xpay.Reposititorys.*;
import com.xpay.Services.PaymentGetway;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WalletService {

    private final WalletRepo walletRepo;
    private final UserRepo userRepo;
    private final ClientRepo clientRepo;
    private final TransactionRepo transactionRepo;
    private final ClientACRepo clientACRepo;
    private final CGSTransactionsRepo clientTransactionRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public WalletService(WalletRepo walletRepo, UserRepo userRepo, ClientRepo clientRepo,
                         TransactionRepo transactionRepo, ClientACRepo clientACRepo,
                         CGSTransactionsRepo clientTransactionRepo, ModelMapper modelMapper) {
        this.walletRepo = walletRepo;
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
        this.transactionRepo = transactionRepo;
        this.clientACRepo = clientACRepo;
        this.clientTransactionRepo = clientTransactionRepo;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ResponseEntity<?> createWallet(String mobile, String apiKey) {
        try {
            if (!isValidMobile(mobile)) {
                return new ResponseEntity<>("Invalid mobile number format", HttpStatus.BAD_REQUEST);
            }

            User user = getUserByMobile(mobile);
            if (user == null) {
                return new ResponseEntity<>("User not found or Invalid Mobile number", HttpStatus.NOT_FOUND);
            }

            Client existingClient = getClientByApiKey(apiKey);
            if (existingClient == null) {
                return new ResponseEntity<>("Client not found with the provided API key", HttpStatus.NOT_FOUND);
            }

            if (!existingClient.isApproved()) {
                return new ResponseEntity<>("This API is blocked", HttpStatus.NOT_FOUND);
            }

            if (walletRepo.existsById(mobile)) {
                return new ResponseEntity<>("Wallet already exists for this mobile number", HttpStatus.ALREADY_REPORTED);
            }

            Wallet newWallet = createNewWallet(mobile, user, existingClient);
            walletRepo.save(newWallet);
            return new ResponseEntity<>("Wallet created successfully", HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage());
            return new ResponseEntity<>("Database constraint violation", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error while creating wallet: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong, please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Cacheable(value = "walletBalance", key = "#userDetails.username")
    public Double getBalance(String username) throws AccountNotFoundException {
        User user = getUserById(username);
        if (user == null) throw  new UsernameNotFoundException("User not found");
        Wallet userWallet = walletRepo.findById(user.getMobile()).orElse(null);
        if (userWallet != null) {
            return userWallet.getBalance();
        }
        throw new AccountNotFoundException("Wallet not found");
    }

    @Transactional
    public ResponseEntity<?> addMoney(Double amount, String username) {
        if (amount <= 0) return new ResponseEntity<>("Amount must be positive and not Zero", HttpStatus.BAD_REQUEST);

        User user = getUserById(username);
        if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        Wallet userWallet = walletRepo.findById(user.getMobile()).orElse(null);
        if (userWallet == null) return new ResponseEntity<>("Wallet not found", HttpStatus.NOT_FOUND);

        if (!userWallet.isActivation()) return new ResponseEntity<>("Account Activation needed", HttpStatus.BAD_REQUEST);

        Transaction transaction = new Transaction();
        Map<String, String> paymentRequest = PaymentGetway.payMoney(amount);

        transaction.setUser(user);
        transaction.setWallet(userWallet);
        transaction.setAmount(amount);
        transaction.setStatus(paymentRequest.get("status"));
        transaction.setTransactionId(paymentRequest.get("transactionId"));
        transaction.setTransactionMethod(paymentRequest.get("transactionType"));
        transaction.setTransactionType("Receive");

        if ("success".equals(paymentRequest.get("status"))) {
            userWallet.setBalance(userWallet.getBalance() + amount);
            transactionRepo.save(transaction);
            walletRepo.save(userWallet);
            return new ResponseEntity<>("Amount Added Successfully " + transaction.getPaymentId().toString(), HttpStatus.OK);
        } else {
            transactionRepo.save(transaction);
            return new ResponseEntity<>("Transaction Failed" + transaction.getPaymentId().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TransactionDTO getTransactionById(String username, String paymentId) throws UsernameNotFoundException{
        User user = getUserById(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        Wallet wallet = walletRepo.findById(user.getMobile()).orElse(null);
        if (wallet == null) throw new UsernameNotFoundException("Wallet not found");
        Transaction transaction = transactionRepo.findById(paymentId).orElse(null);
        if(transaction != null){
        if(transaction.getWallet() != wallet) return null;
        return modelMapper.map(transaction,TransactionDTO.class);
        }
        return null;
    }


    @Cacheable(value = "userTransactions", key = "#userDetails.username")
    public List<TransactionDTO> getUserTransactions(String username) throws UsernameNotFoundException{
        User user = getUserById(username);
        if (user == null) throw new UsernameNotFoundException("User not found");

        Wallet wallet = walletRepo.findById(user.getMobile()).orElse(null);
        if (wallet == null) throw new UsernameNotFoundException("Wallet not found");
        List<Transaction> transactions = transactionRepo.findAllByWallet(wallet);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
        return transactionDTOs;
    }

    @Transactional
    public ResponseEntity<?> payFor(String username, Double amount, String productCategory, String productName, String apiKey) {
        if (amount <= 0) return new ResponseEntity<>("Negative Amount can't be debited", HttpStatus.BAD_REQUEST);

        User user = getUserById(username);
        if (user == null) return new ResponseEntity<>("User not found or Invalid Mobile number", HttpStatus.NOT_FOUND);

        Client existingClient = getClientByApiKey(apiKey);
        if (existingClient == null) return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);

        if (!existingClient.isApproved()) return new ResponseEntity<>("This API is blocked", HttpStatus.NOT_FOUND);

        Wallet userWallet = walletRepo.findById(user.getMobile()).orElse(null);
        if (userWallet == null) return new ResponseEntity<>("Wallet not found", HttpStatus.NOT_FOUND);

        if (userWallet.getBalance() < amount) return new ResponseEntity<>("Insufficient Amount", HttpStatus.BAD_REQUEST);

        ClientAc clientAc = clientACRepo.findByClient(existingClient);
        if (clientAc != null) clientAc.setBalance(clientAc.getBalance() + amount);

        Transaction userTransaction = createUserTransaction(amount, userWallet, user);
        CGSTransactions clientTransaction = createClientTransaction(amount, productCategory, productName, existingClient, user);

        walletRepo.save(userWallet);
        clientACRepo.save(clientAc);
        transactionRepo.save(userTransaction);
        clientTransactionRepo.save(clientTransaction);

        return new ResponseEntity<>("Amount paid successfully", HttpStatus.CREATED);
    }

    // Helper methods for repetitive tasks
    private boolean isValidMobile(String mobile) {
        return mobile.matches("^[0-9]{10}$");
    }

    private User getUserByMobile(String mobile) {
        return userRepo.findByMobile(mobile);
    }

    private User getUserById(String username) {
        return userRepo.findById(username).orElse(null);
    }

    private Client getClientByApiKey(String apiKey) {
        return clientRepo.findByApiKey(apiKey);
    }

    private Wallet createNewWallet(String mobile, User user, Client existingClient) {
        Wallet wallet = new Wallet();
        wallet.setMobile(mobile);
        wallet.setUser(user);
        wallet.setClient(existingClient);
        wallet.setActivation(false);
        return wallet;
    }

    private Transaction createUserTransaction(Double amount, Wallet userWallet, User user) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setWallet(userWallet);
        transaction.setUser(user);
        transaction.setTransactionType("Pay");
        transaction.setStatus("success");
        transaction.setTransactionMethod("wallet");
        return transaction;
    }

    private CGSTransactions createClientTransaction(Double amount, String productCategory, String productName, Client existingClient, User user) {
        CGSTransactions clientTransaction = new CGSTransactions();
        clientTransaction.setClient(existingClient);
        clientTransaction.setUser(user);
        clientTransaction.setAmount(amount);
        clientTransaction.setStatus("success");
        clientTransaction.setProductCategory(productCategory);
        clientTransaction.setProductName(productName);
        return clientTransaction;
    }
}
