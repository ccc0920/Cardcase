package com.cardcase.server.repository;

import jakarta.persistence.*;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @Column(name="order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long order_id;

    @Column(name="user_id")
    private int user_id;

    @Column(name="card_id")
    private long card_id;

    @Column(name="materials")
    private String materials;

    @Column(name="quantity")
    private int quantity = 1;

    @Column(name="contact_info")
    private String contact_info;

    @Column(name="payment_method")
    private String payment_method;

    @Column(name="state")
    private String state = "用户未支付，商家未发货";

    public Order() {
    }

    public Order(int user_id, long card_id, String materials, int quantity, String contact_info, String payment_method) {
        this.user_id = user_id;
        this.card_id = card_id;
        this.materials = materials;
        this.quantity = quantity;
        this.contact_info = contact_info;
        this.payment_method = payment_method;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public long getCard_id() {
        return card_id;
    }

    public void setCard_id(long card_id) {
        this.card_id = card_id;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
