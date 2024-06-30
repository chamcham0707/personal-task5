package com.sparta.ottoon.post.entity;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PostStatus postStatus;

    @Column(nullable = false)
    private boolean isTop;

    private Long likeCount = 0L;

    public Post(String contents) {
        this.contents = contents;
        this.postStatus = PostStatus.GENERAL;
    }

    public Post(String contents, User user) {
        this.contents = contents;
        this.postStatus = PostStatus.GENERAL;
        this.user = user;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void update(String contents) {
        this.contents = contents;
    }

    public void noticed() {
        if (this.getPostStatus().equals(PostStatus.NOTICE)) {
            this.postStatus = PostStatus.GENERAL;
            this.isTop = false;
        } else {
            this.postStatus = PostStatus.NOTICE;
            this.isTop = true;
        }
    }

    public void increaseLikeCount() {
        ++this.likeCount;
    }

    public void decreaseLikecount() {
        --this.likeCount;
    }
}