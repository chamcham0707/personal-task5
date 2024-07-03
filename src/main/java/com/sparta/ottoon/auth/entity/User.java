package com.sparta.ottoon.auth.entity;

import com.sparta.ottoon.common.Timestamped;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.profile.dto.ProfileRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
public class User extends Timestamped implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length = 50)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(length = 100)
    private String nickname;
    @Column(length = 100)
    private String intro;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;
    @OneToMany
    private List<Post> posts = new ArrayList<>();
    @Column
    private String refreshToken;
    private Long kakaoId;
    private int postLikeCount = 0;
    private int commentLikeCount = 0;
    private int follower = 0;

    public User(String username, String nickname, String password, String email, UserStatus status) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.status = status;
    }

    @Builder
    public User(String username, String nickname, String encodedPassword, String email, UserStatus userStatus, Long kakaoId, String refresh) {
        this.username = username;
        this.nickname = nickname;
        this.password = encodedPassword;
        this.email = email;
        this.status = userStatus;
        this.refreshToken = refresh;
        this.kakaoId = kakaoId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserStatus status = this.getStatus();
        String authority = status.getStatus();

        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(grantedAuthority);

        return grantedAuthorities;
    }

    public void updatePost(Post post) {
        this.posts.add(post);
    }

    public void updateRefresh(String refresh) {
        this.refreshToken = refresh;
    }

    public void updateUserInfo(ProfileRequestDto requestDto){
        this.nickname = requestDto.getNickname();
        this.intro = requestDto.getIntro();
    }

    public void updateUserPassword(String newPassword) {
        this.password = newPassword;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void updateStatus(UserStatus userStatus){
        this.status =  userStatus;
            }

    public void increasePostLikeCount() {
        ++this.postLikeCount;
    }

    public void decreasePostLikeCount() {
        if (this.postLikeCount <= 0) {
            throw new IllegalStateException("User::postLikeCount is 0 or under");
        }
        --this.postLikeCount;
    }

    public void increaseCommentLikeCount() {
        ++this.commentLikeCount;
    }

    public void decreaseCommentLikeCount() {
        if (this.commentLikeCount <= 0) {
            throw new IllegalStateException("User::commentLikeCount is 0 or under");
        }
        --this.commentLikeCount;
    }

    public void increaseFollower() {
        ++this.follower;
    }

    public void decreaseFollower() {
        if (this.follower <= 0) {
            throw new IllegalStateException("User::follower is 0 or under");
        }
        --this.follower;
    }
}