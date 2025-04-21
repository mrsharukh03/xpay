package com.xpay.Security.OAuth;

import com.xpay.Entitys.User.User;
import com.xpay.Reposititorys.UserRepo;
import com.xpay.Security.JWT.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");
        String profileUrl = oauth2User.getAttribute("picture");

        Optional<User> existingUser = userRepo.findById(email);

        Map<String, String> responseMap = new HashMap<>();
        String token = jwtUtils.generateUserToken(email, "USER");

        if (existingUser.isPresent()) {
            // If user exists, generate token and respond
            responseMap.put("token", token);
            response.setStatus(HttpStatus.OK.value());
        } else {
            // If user doesn't exist, create new user and generate token
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(fullName);
            newUser.setProfileUrl(profileUrl);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            try {
                userRepo.save(newUser);
                responseMap.put("token", token);
                response.setStatus(HttpStatus.OK.value());
            } catch (Exception e) {
                // Log the error and return internal server error
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("Something went wrong: " + e.getMessage());
                return;
            }
        }

        // Set content type and return the response as JSON
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseMap));
    }
}
