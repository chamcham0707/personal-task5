package com.sparta.ottoon.comment.dto;

import com.sparta.ottoon.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

    public CommentResponseDto(Comment comment, Long likeCount) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.likeCount = likeCount;
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
