package com.sparta.ottoon.follow.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.post.entity.Post;

import java.util.List;

public interface FollowRepositoryCustom {
    List<Post> findByAllFollowPostList(User followUser, int pageNumber, int pageSize, OrderSpecifier<?> orderSpecifier);
}
