package com.xpay.Services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentGetway {

    public static Map<String, String> payMoney(Double amount) {
        Map<String,String> response = new HashMap<>();
        if(simulatePaymentSuccess()){
            response.put("status","success");
        }else {
            response.put("status","failed");
        }
        response.put("transactionType",getRandomPaymentMethod());
        response.put("transactionId", UUID.randomUUID().toString());
        return response;
    }

    private static boolean simulatePaymentSuccess() {
        return Math.random() > 0.2; // 80% chance of success, 20% chance of failure.
    }

     private static String getRandomPaymentMethod() {
        String[] PAYMENT_METHODS = {"UPI", "Debit Card", "Credit Card", "Wallet"};
        Random random = new Random();
        int randomIndex = random.nextInt(PAYMENT_METHODS.length);
        return PAYMENT_METHODS[randomIndex];
        }
    }
