package com.sparta.ottoon.profile.dto;

import com.sparta.ottoon.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProfileResponseDto {
    private String username;
    private String email;
    private String nickname;
    private String intro;
    private String status;
    private int postLikeCount = 0;
    private int commentLikeCount = 0;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
        this.status = user.getStatus().getStatus();
        this.postLikeCount = user.getPostLikeCount();
        this.commentLikeCount = user.getCommentLikeCount();
    }
}
