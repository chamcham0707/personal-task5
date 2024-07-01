package com.sparta.ottoon.follow.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ottoon.auth.entity.QUser;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.follow.entity.QFollow;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findByAllFollowPostList(User followUser, int pageNumber, int pageSize, OrderSpecifier<?> orderSpecifier) {
        QUser user = QUser.user;
        QFollow follow = QFollow.follow;
        QPost post = QPost.post;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return jpaQueryFactory
                .selectFrom(post)
                .join(follow).on(user.id.eq(follow.followedUserId))
                .join(post).on(follow.followedUserId.eq(post.user.id))
                .where(follow.followUser.id.eq(followUser.getId()))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<User> findTopTen() {
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .orderBy(user.follower.desc())
                .limit(10)
                .fetch();
    }
}
