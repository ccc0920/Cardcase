package com.cardcase.server.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CardId implements Serializable {
    @Column(name = "card_id")
    private long card_id;

    @Column(name = "element_type")
    private String element_type;

    public CardId() {
    }

    public CardId(long card_id, String element_type) {
        this.card_id = card_id;
        this.element_type = element_type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardId cardId = (CardId) o;
        return card_id == cardId.card_id && Objects.equals(element_type, cardId.element_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card_id, element_type);
    }

    public long getCard_id() {
        return card_id;
    }

    public void setCard_id(long card_id) {
        this.card_id = card_id;
    }

    public String getElement_type() {
        return element_type;
    }

    public void setElement_type(String element_type) {
        this.element_type = element_type;
    }
}
