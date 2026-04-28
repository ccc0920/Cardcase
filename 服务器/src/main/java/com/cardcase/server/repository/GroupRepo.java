package com.cardcase.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepo extends JpaRepository<CardGroup, Integer> {
    List<CardGroup> findByUserId(int userId);

    List<CardGroup> findByUserIdAndGroupName(int userId, String groupName);

    void deleteByUserIdAndCardId(int userId, long cardId);
}
