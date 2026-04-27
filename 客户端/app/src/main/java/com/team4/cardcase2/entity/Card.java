package com.team4.cardcase2.entity;
import java.util.ArrayList;
import java.util.List;

public class Card {
    private int card_id;
    private List<Element> elements = new ArrayList<Element>();

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public Card() {
    }
}