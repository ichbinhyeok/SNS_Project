package com.example.sns_project.repository;

import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT CASE WHEN f.user1.id = :userId THEN f.user2 ELSE f.user1 END FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2) OR (f.user1.id = :userId2 AND f.user2.id = :userId1)")
    Optional<Friendship> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);


    //TODO 공부 많이 하자 신혁아!! 웅
    // 친구 추가
    Friendship findByUser1AndUser2(User user1, User user2);
}
