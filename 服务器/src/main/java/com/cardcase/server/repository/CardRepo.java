package com.cardcase.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepo extends JpaRepository<Card, CardId> {
    @Query("SELECT c FROM Card c WHERE c.id.card_id = :card_id")
    List<Card> findAllByCardId(@Param("card_id") long card_id);
}