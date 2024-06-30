package com.sparta.ottoon.like.entity;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.post.entity.Post;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts_like")
@NoArgsConstructor
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
