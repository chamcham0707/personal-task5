package com.sparta.ottoon.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.like.entity.QPostLike;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findByUserReturnPosts(User user, int pageNumber, int pageSize) {
        QPost post = QPost.post;
        QPostLike postLike = QPostLike.postLike;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return jpaQueryFactory
                .select(post)
                .from(postLike)
                .join(postLike.post, post)
                .where(postLike.user.id.eq(user.getId()))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
