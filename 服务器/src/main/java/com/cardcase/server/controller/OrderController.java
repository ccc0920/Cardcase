package com.cardcase.server.controller;

import com.cardcase.server.service.JwtService;
import com.cardcase.server.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/orders")
    public String OrderCreation(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String request) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{\"success\":false,\"message\":\"Jwt token verification failed.\"}";
        JsonNode rootNode = objectMapper.readTree(request);
        long orderId = orderService.insertOrder(rootNode.get("userId").asInt(), rootNode.get("cardId").asLong(), rootNode.get("materials").asText(), rootNode.get("quantity").asInt(), rootNode.get("contactInfo").toString(), rootNode.get("paymentMethod").toString());
        if(orderId != 0) {
            return "{\"success\":true,\"orderId\":" + orderId + ",\"message\":\"Order placed successfully.\"}";
        } else {
            return "{\"success\":false,\"message\":\"Order placement failed.\"}";
        }
    }
}
