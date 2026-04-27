package com.cardcase.server.service;

import com.cardcase.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private UserCardRepo userCardRepo;

    @Override
    public long insertCard(int userId, String avatar, String background, String design) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null || user.getUsertype() == 0) {
            return 0;
        } else {
            UserCard usercard = new UserCard(userId, Base64.getDecoder().decode(avatar), Base64.getDecoder().decode(background), design);
            userCardRepo.save(usercard);
            return usercard.getCard_id();
        }
    }

    @Override
    public int updateCard(long cardId, String avatar, String background, String design) {
        UserCard usercard = userCardRepo.findById(cardId).orElse(null);
        if (usercard == null) {
            return 0;
        } else {
            usercard.setAvatar(Base64.getDecoder().decode(avatar));
            usercard.setBackground(Base64.getDecoder().decode(background));
            usercard.setDesign(design);
            userCardRepo.save(usercard);
            return usercard.getUser_id();
        }
    }

    @Override
    public void insertElement(CardId id, String content, int position_x, int position_y, String style) {
        Card card = new Card(id, content, position_x, position_y, style);
        cardRepo.save(card);
    }

    @Override
    public void updateElement(CardId id, String content, int position_x, int position_y, String style) {
        Card card = cardRepo.findById(id).orElse(null);
        if (card == null) {
            insertElement(id, content, position_x, position_y, style);
        } else {
            card.setContent(content);
            card.setPosition_x(position_x);
            card.setPosition_y(position_y);
            card.setStyle(style);
            cardRepo.save(card);
        }
    }

    @Override
    public String getCardById(long cardId) {
        UserCard usercard = userCardRepo.findById(cardId).orElse(null);
        if (usercard == null) {
            return null;
        } else {
            String cardInfo = "{\"cardId\":" + usercard.getCard_id() + ",\"userId\":" + usercard.getUser_id() + ",\"avatar\":\"" + Base64.getEncoder().encodeToString(usercard.getAvatar()) + "\",\"background\":\"" + Base64.getEncoder().encodeToString(usercard.getBackground()) + "\",\"design\":" + usercard.getDesign() + ",\"elements\":";
            ArrayList<String> stringList = new ArrayList<>();
            List<Card> cardList = cardRepo.findAllByCardId(cardId);
            for (Card element : cardList) {
                stringList.add("{\"type\":\"" + element.getId().getElement_type() + "\",\"content\":\"" + element.getContent() + "\",\"position\":{\"x\":" + element.getPosition_x() + ",\"y\":" + element.getPosition_y() + "},\"style\":" + element.getStyle() + "}");
            }
            cardInfo += stringList.toString() + "}";
            return cardInfo;
        }
    }

    @Override
    public boolean deleteCardById(long cardId) {
        UserCard usercard = userCardRepo.findById(cardId).orElse(null);
        if (usercard == null) {
            return false;
        } else {
            List<Card> cardList = cardRepo.findAllByCardId(cardId);
            cardRepo.deleteAllInBatch(cardList);
            userCardRepo.delete(usercard);
            return true;
        }
    }

    @Override
    public String getCardsByUserId(int userId) {
        List<UserCard> usercards = userCardRepo.findAllByUserId(userId);
        ArrayList<String> cardInfoList = new ArrayList<>();
        for (UserCard usercard : usercards) {
            String cardInfo = getCardById(usercard.getCard_id());
            if (cardInfo != null) cardInfoList.add(cardInfo);
        }
        return "[" + String.join(",", cardInfoList) + "]";
    }
}
