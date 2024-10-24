package com.example.sns_project.repository;

import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<User> findFriendsByUserId(Long userId);
    Optional<Friendship> findByUserIds(Long user1Id, Long user2Id);
}
