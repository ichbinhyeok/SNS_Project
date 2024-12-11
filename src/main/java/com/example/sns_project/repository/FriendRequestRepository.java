package com.example.sns_project.repository;

import com.example.sns_project.enums.RequestStatus;
import com.example.sns_project.model.FriendRequest;
import com.example.sns_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverId(Long receiverId);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, RequestStatus status);}
