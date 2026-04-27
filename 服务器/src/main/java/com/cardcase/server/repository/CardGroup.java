package com.cardcase.server.repository;

import jakarta.persistence.*;

@Entity
@Table(name="card_groups")
public class CardGroup {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    private int userId;

    @Column(name="card_id")
    private long cardId;

    @Column(name="group_name")
    private String groupName;

    public CardGroup() {
    }

    public CardGroup(int userId, long cardId, String groupName) {
        this.userId = userId;
        this.cardId = cardId;
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
