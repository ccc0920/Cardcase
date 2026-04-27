package com.cardcase.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepo extends JpaRepository<UserCard, Long> {
    @Query("SELECT uc FROM UserCard uc WHERE uc.user_id = :userId")
    List<UserCard> findAllByUserId(@Param("userId") int userId);
}
