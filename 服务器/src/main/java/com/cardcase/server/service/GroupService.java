package com.cardcase.server.service;

import com.cardcase.server.repository.CardGroup;

import java.util.List;

public interface GroupService {
    boolean createGroup(int userId, String groupName);
    boolean addCardToGroup(int userId, long cardId, String groupName);
    boolean removeCardFromGroup(int userId, long cardId);
    boolean moveCardToGroup(int userId, long cardId, String newGroupName);
    List<CardGroup> getGroupsByUserId(int userId);
    List<CardGroup> getCardsInGroup(int userId, String groupName);
    List<String> getGroupNames(int userId);
}
