package com.sparta.ottoon.like.entity;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments_like")
@NoArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public CommentLike(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
