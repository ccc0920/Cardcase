package com.cardcase.server.service;

import com.cardcase.server.repository.CardGroup;
import com.cardcase.server.repository.GroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepo groupRepo;

    @Override
    public boolean createGroup(int userId, String groupName) {
        if (groupRepo.findByUserIdAndGroupName(userId, groupName).isEmpty()) {
            CardGroup group = new CardGroup(userId, -1, groupName);
            groupRepo.save(group);
            return true;
        }
        return false;
    }

    @Override
    public boolean addCardToGroup(int userId, long cardId, String groupName) {
        groupRepo.deleteByUserIdAndCardId(userId, cardId);
        CardGroup group = new CardGroup(userId, cardId, groupName);
        groupRepo.save(group);
        return true;
    }

    @Override
    public boolean removeCardFromGroup(int userId, long cardId) {
        groupRepo.deleteByUserIdAndCardId(userId, cardId);
        return true;
    }

    @Override
    public boolean moveCardToGroup(int userId, long cardId, String newGroupName) {
        groupRepo.deleteByUserIdAndCardId(userId, cardId);
        CardGroup group = new CardGroup(userId, cardId, newGroupName);
        groupRepo.save(group);
        return true;
    }

    @Override
    public List<CardGroup> getGroupsByUserId(int userId) {
        return groupRepo.findByUserId(userId);
    }

    @Override
    public List<CardGroup> getCardsInGroup(int userId, String groupName) {
        return groupRepo.findByUserIdAndGroupName(userId, groupName);
    }

    @Override
    public List<String> getGroupNames(int userId) {
        return groupRepo.findByUserId(userId).stream()
                .map(CardGroup::getGroupName)
                .distinct()
                .collect(Collectors.toList());
    }
}
