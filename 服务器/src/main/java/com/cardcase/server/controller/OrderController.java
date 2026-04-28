package com.cardcase.server.controller;

import com.cardcase.server.repository.Order;
import com.cardcase.server.service.JwtService;
import com.cardcase.server.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        JsonNode rootNode = objectMapper.readTree(request);
        long orderId = orderService.insertOrder(rootNode.get("userId").asInt(), rootNode.get("cardId").asLong(), rootNode.get("materials").asText(), rootNode.get("quantity").asInt(), rootNode.get("contactInfo").toString(), rootNode.get("paymentMethod").toString());
        if(orderId != 0) {
            return "{"success":true,"orderId":" + orderId + ","message":"Order placed successfully."}";
        } else {
            return "{"success":false,"message":"Order placement failed."}";
        }
    }

    @GetMapping("/orders/user/{userId}")
    public String GetOrdersByUser(@RequestHeader(name = "Authorization") String authHeader, @PathVariable int userId) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        List<Order> orders = orderService.getOrdersByUserId(userId);
        ArrayNode ordersArray = objectMapper.createArrayNode();
        for (Order order : orders) {
            ObjectNode orderNode = objectMapper.createObjectNode();
            orderNode.put("orderId", order.getOrder_id());
            orderNode.put("cardId", order.getCard_id());
            orderNode.put("materials", order.getMaterials());
            orderNode.put("quantity", order.getQuantity());
            orderNode.put("contactInfo", order.getContact_info());
            orderNode.put("paymentMethod", order.getPayment_method());
            orderNode.put("state", order.getState());
            ordersArray.add(orderNode);
        }
        return "{"success":true,"orders":" + ordersArray.toString() + "}";
    }

    @GetMapping("/orders/{orderId}")
    public String GetOrderById(@RequestHeader(name = "Authorization") String authHeader, @PathVariable long orderId) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "{"success":false,"message":"Order not found."}";
        }
        ObjectNode orderNode = objectMapper.createObjectNode();
        orderNode.put("orderId", order.getOrder_id());
        orderNode.put("cardId", order.getCard_id());
        orderNode.put("materials", order.getMaterials());
        orderNode.put("quantity", order.getQuantity());
        orderNode.put("contactInfo", order.getContact_info());
        orderNode.put("paymentMethod", order.getPayment_method());
        orderNode.put("state", order.getState());
        return "{"success":true,"order":" + orderNode.toString() + "}";
    }

    @PutMapping("/orders/{orderId}/cancel")
    public String CancelOrder(@RequestHeader(name = "Authorization") String authHeader, @PathVariable long orderId) throws JsonProcessingException {
        if (!jwtService.checkToken(authHeader)) return "{"success":false,"message":"Jwt token verification failed."}";
        boolean success = orderService.updateOrderStatus(orderId, "已取消");
        if (success) {
            return "{"success":true,"message":"Order cancelled successfully."}";
        } else {
            return "{"success":false,"message":"Order cancellation failed."}";
        }
    }
}