package com.cardcase.server.service;

import com.cardcase.server.repository.CardId;

public interface CardService {
    long insertCard(int userId, String avatar, String background, String design);

    int updateCard(long cardId, String avatar, String background, String design);

    void insertElement(CardId id, String content, int position_x, int position_y, String style);

    void updateElement(CardId id, String content, int position_x, int position_y, String style);

    String getCardById(long cardId);

    boolean deleteCardById(long cardId);

    String getCardsByUserId(int userId);
}
