package com.example.sns_project.repository;

import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.model.Friendship;
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT new com.example.sns_project.dto.UserDTO(u.id, u.username, u.email) FROM User u JOIN Friendship f ON (f.user1 = u OR f.user2 = u) WHERE (f.user1.id = :userId AND u.id = f.user2.id) OR (f.user2.id = :userId AND u.id = f.user1.id)")
    List<UserDTO> findFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2) OR (f.user1.id = :userId2 AND f.user2.id = :userId1)")
    Optional<Friendship> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
            "(f.user1.id = :id1 AND f.user2.id = :id2) OR " +
            "(f.user1.id = :id2 AND f.user2.id = :id1)")
    boolean existsFriendship(@Param("id1") Long id1, @Param("id2") Long id2);

}
