package com.cardcase.server.service;

import com.cardcase.server.repository.Order;

import java.util.List;

public interface OrderService {
    long insertOrder(int userId, long cardId, String materials, int quantity, String contactInfo, String paymentMethod);

    List<Order> getOrdersByUserId(int userId);

    Order getOrderById(long orderId);

    boolean updateOrderStatus(long orderId, String status);
}
