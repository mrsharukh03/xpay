package com.xpay.Services.OTPServices;

import com.xpay.Entitys.Client.OTPDetails;
import com.xpay.Reposititorys.OTPDetailsRepo;
import com.xpay.Services.MailServices.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
public class OTPServices {

    private static OTPDetailsRepo otpDetailsRepo;
    private static MailService mailService;

    private static final int MAX_OTP_REQUESTS = 2;
    private static final int OTP_REQUEST_TIME_LIMIT = 24;

    @Autowired
    public OTPServices(OTPDetailsRepo otpDetailsRepo, MailService mailService) {
        OTPServices.otpDetailsRepo = otpDetailsRepo;
        OTPServices.mailService = mailService;
    }

    public static boolean alreadyActiveOTP(String email) {
        try {
            OTPDetails otpDetails = otpDetailsRepo.findById(email).orElse(null);
            if (otpDetails != null && LocalDateTime.now().isBefore(otpDetails.getExpirationTime())) {
                return true;
            }
        } catch (Exception e) {
            log.error("Error OTP checking {}", e.getMessage());
        }
        return false;
    }

    public static Map<String, String> sendOTP(String email) {
        Map<String,String> response = new HashMap<>();

        // Generate a new OTP
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        String otpString = String.valueOf(otp);

        // Set OTP expiration time (2 minutes from now)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2);

        try {
            // Check if there is an active OTP for the user
            if (alreadyActiveOTP(email)) {
                response.put("status", "false");
                response.put("msg", "You already have a valid OTP.");
                return response;
            }

            OTPDetails otpDetails = otpDetailsRepo.findById(email).orElse(null);

            if (otpDetails != null) {
                // Reset the counter if 24 hours have passed since last request
                if (otpDetails.getLastRequestTime() != null &&
                        otpDetails.getLastRequestTime().plusHours(OTP_REQUEST_TIME_LIMIT).isBefore(LocalDateTime.now())) {
                    otpDetails.setRequestCount(0);  // Reset request count after 24 hours
                    otpDetails.setLastRequestTime(LocalDateTime.now());  // Update last request time
                    otpDetailsRepo.save(otpDetails); // Save the updated OTPDetails
                }

                // Check if the user has exceeded the OTP request limit
                if (otpDetails.getRequestCount() >= MAX_OTP_REQUESTS) {
                    log.warn("OTP request limit exceeded for user: {}", email);
                    response.put("status", "false");
                    response.put("msg", "You have reached the maximum OTP request limit. Try again after 24 hours.");
                    return response;
                }

                // Increment request count, update last request time, and set OTP
                otpDetails.setRequestCount(otpDetails.getRequestCount() + 1);
                otpDetails.setLastRequestTime(LocalDateTime.now());
                otpDetails.setOtp(otpString);
                otpDetails.setExpirationTime(expirationTime);
                boolean isSent = mailService.sendOTPEmail(email, otpString);
                if(isSent){
                 otpDetailsRepo.save(otpDetails); // Save the updated OTPDetails
                    response.put("status", "true");
                    response.put("msg", "OTP Sent successfully to your email");
                    return response;
                }
            } else {
                // Create new OTPDetails if no record exists
                otpDetails = new OTPDetails(email, otpString, LocalDateTime.now(), expirationTime, 1, LocalDateTime.now());
               boolean isSent =  mailService.sendOTPEmail(email, otpString);

               if(isSent) {
                   otpDetailsRepo.save(otpDetails); // Save new OTPDetails
                   response.put("status", "true");
                   response.put("msg", "OTP successfully sent to your email.");
                   return response;
               }
            }
                   response.put("status", "false");
                   response.put("msg", "OTP sending filed.");
        } catch (Exception e) {
            log.error("Error while sending OTP: {}", e.getMessage());
            response.put("status", "false");
            response.put("msg", "An error occurred while sending OTP.");
        }

        return response;
    }



    public static Map<String, String> verifyOTP(String email, String otp) {
        Map<String,String> response = new HashMap<>();
        try {
            OTPDetails otpDetails = otpDetailsRepo.findById(email).orElse(null);
            if (otpDetails != null) {
                if(otpDetails.getOtp().equals(otp)){
                    if(LocalDateTime.now().isBefore(otpDetails.getExpirationTime())){
                        otpDetailsRepo.delete(otpDetails);
                        response.put("status","true");
                        response.put("msg","Verification Done");
                        return response;
                    }else{
                        response.put("status","false");
                        response.put("msg","OTP Expired");
                    }
                }else{
                    response.put("status","false");
                    response.put("msg","Invalid OTP ");
                }
            }else{
                response.put("status","false");
                response.put("msg","No Verification Request Found");
            }
        } catch (Exception e) {
            log.error("Error while verifying OTP: {}", e.getMessage());
        }
        return response;
    }
}
