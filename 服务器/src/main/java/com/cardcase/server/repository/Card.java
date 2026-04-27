package com.cardcase.server.repository;

import jakarta.persistence.*;

@Entity
@Table(name="cards")
public class Card {
    @EmbeddedId
    private CardId id;

    @Column(name="content")
    private String content = null;

    @Column(name="position_x")
    private int position_x;

    @Column(name="position_y")
    private int position_y;

    @Column(name="style")
    private String style = null;

    public Card() {
    }

    public Card(CardId id, String content, int position_x, int position_y, String style) {
        this.id = id;
        this.content = content;
        this.position_x = position_x;
        this.position_y = position_y;
        this.style = style;
    }

    public CardId getId() {
        return id;
    }

    public void setId(CardId id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPosition_x() {
        return position_x;
    }

    public void setPosition_x(int position_x) {
        this.position_x = position_x;
    }

    public int getPosition_y() {
        return position_y;
    }

    public void setPosition_y(int position_y) {
        this.position_y = position_y;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
