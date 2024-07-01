package com.sparta.ottoon.follow.repository;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {
    Optional<Follow> findByFollowUserAndFollowedUserId(User followedUser, long userId);
}
