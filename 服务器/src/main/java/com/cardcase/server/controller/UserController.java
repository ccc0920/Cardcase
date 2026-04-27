package com.cardcase.server.controller;

import com.cardcase.server.service.JwtService;
import com.cardcase.server.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/register")
    public String UserRegistration(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        String vcode = userService.insertUser(email, rootNode.get("password").asText());
        if(vcode != null) {
            if (userService.sendEmail(email, "CardCase验证消息", "验证码：<img src='cid:imageId' alt='image'/>", userService.vcodeToImage(vcode))) {
                return "{\"success\":true,\"message\":\"User registered successfully. Verification code sent to email.\"}";
            } else {
                return "{\"success\":false,\"message\":\"Failed to send email.\"}";
            }
        } else {
            return "{\"success\":false,\"message\":\"This email has been registered.\"}";
        }
    }

    @PostMapping("/verify-email")
    public String EmailVerification(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        String vcode = userService.getCodeByEmail(email);
        if(rootNode.get("verificationCode").asText().equalsIgnoreCase(vcode)) {
            int userId = userService.setTypeByEmail(email);
            return "{\"success\":true,\"userId\":" + userId + ",\"message\":\"Email verified successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Email verification failed.\"}";
        }
    }

    @PostMapping("/resend-verification-code")
    public String CodeResend(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        String vcode = userService.setCodeByEmail(email);
        if(vcode != null) {
            if (userService.sendEmail(email, "CardCase验证消息", "验证码：<img src='cid:imageId' alt='image'/>", userService.vcodeToImage(vcode))) {
                return "{\"success\":true,\"message\":\"Verification code resent to email.\"}";
            } else {
                return "{\"success\":false,\"message\":\"Failed to send email.\"}";
            }
        } else {
            return "{\"success\":false,\"message\":\"User does not exist.\"}";
        }
    }

    @PostMapping("/login")
    public String UserLogin(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        int state = userService.verifyPassword(email, rootNode.get("password").asText());
        if (state == 0) {
            int userId = userService.setTypeByEmail(email);
            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            String token = jwtService.createToken(map);
            return "{\"success\":true,\"userId\":" + userId + ",\"token\":\""+ token + "\",\"message\":\"User logged in successfully.\"}";
        } else if (state == 1) {
            return "{\"success\":false,\"message\":\"Password error.\"}";
        } else {
            return "{\"success\":false,\"message\":\"User does not exist.\"}";
        }
    }

    @PostMapping("/forgot-password")
    public String PasswordResetEmail(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        String vcode = userService.setCodeByEmail(email);
        if(vcode != null) {
            if (userService.sendEmail(email, "CardCase密码重置", "验证码：<img src='cid:imageId' alt='image'/>", userService.vcodeToImage(vcode))) {
                return "{\"success\":true,\"message\":\"Verification code sent to email.\"}";
            } else {
                return "{\"success\":false,\"message\":\"Failed to send email.\"}";
            }
        } else {
            return "{\"success\":false,\"message\":\"User does not exist.\"}";
        }
    }

    @PostMapping("/reset-password")
    public String PasswordReset(@RequestBody String request) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(request);
        String email = rootNode.get("email").asText();
        String vcode = userService.getCodeByEmail(email);
        if(rootNode.get("resetCode").asText().equalsIgnoreCase(vcode)) {
            userService.setPwdByEmail(email, rootNode.get("newPassword").asText());
            return "{\"success\":true,\"message\":\"Password reset successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Password reset failed.\"}";
        }
    }
}
