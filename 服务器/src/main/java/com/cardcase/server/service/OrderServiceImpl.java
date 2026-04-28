package com.cardcase.server.service;

import com.cardcase.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private UserCardRepo userCardRepo;

    @Override
    public long insertOrder(int userId, long cardId, String materials, int quantity, String contactInfo, String paymentMethod) {
        UserCard usercard = userCardRepo.findById(cardId).orElse(null);
        if (usercard == null || usercard.getUser_id() != userId) {
            return 0;
        } else {
            Order order = new Order(userId, cardId, materials, quantity, contactInfo, paymentMethod);
            orderRepo.save(order);
            return order.getOrder_id();
        }
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepo.findByUser_id(userId);
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    @Override
    public boolean updateOrderStatus(long orderId, String status) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        order.setState(status);
        orderRepo.save(order);
        return true;
    }
}
