package com.cardcase.server.repository;

import jakarta.persistence.*;

@Entity
@Table(name="user_card")
public class UserCard {
    @Id
    @Column(name="card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long card_id;

    @Column(name="user_id")
    private int user_id;

    @Column(name="avatar", columnDefinition = "MEDIUMBLOB")
    private byte[] avatar;

    @Column(name="background", columnDefinition = "MEDIUMBLOB")
    private byte[] background;

    @Column(name="design")
    private String design = null;

    public UserCard() {
    }

    public UserCard(int user_id, byte[] avatar, byte[] background, String design) {
        this.user_id = user_id;
        this.avatar = avatar;
        this.background = background;
        this.design = design;
    }

    public long getCard_id() {
        return card_id;
    }

    public void setCard_id(long card_id) {
        this.card_id = card_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public byte[] getBackground() {
        return background;
    }

    public void setBackground(byte[] background) {
        this.background = background;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }
}
