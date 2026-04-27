package com.cardcase.server.service;

public interface OrderService {
    long insertOrder(int userId, long cardId, String materials, int quantity, String contactInfo, String paymentMethod);
}
