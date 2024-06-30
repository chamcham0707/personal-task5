package com.sparta.ottoon.like.repository;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.post.entity.Post;

import java.util.List;

public interface PostLikeRepositoryCustom {
    List<Post> findByUserReturnPosts(User user, int pageNumber, int pageSize);
}
