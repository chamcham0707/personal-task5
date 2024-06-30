package com.sparta.ottoon.like.repository;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.comment.entity.Comment;

import java.util.List;

public interface CommentLikeRepositoryCustom {
    List<Comment> findByUserReturnComments(User user, int pageNumber, int pageSize);
}
