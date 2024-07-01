package com.sparta.ottoon.follow.entity;

import com.sparta.ottoon.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "followed_user_id",nullable = false)
    Long followedUserId;

    @ManyToOne
    @JoinColumn(name = "follow_user_id")
    User followUser;

    public Follow(User followUser, Long followedUserId) {
        this.followUser = followUser;
        this.followedUserId = followedUserId;
    }
}
