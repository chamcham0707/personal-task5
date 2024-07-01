package com.sparta.ottoon.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.comment.entity.QComment;
import com.sparta.ottoon.like.entity.QCommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CommentLikeRepositoryCustomImpl implements CommentLikeRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findByUserReturnComments(User user, int pageNumber, int pageSize) {
        QComment comment = QComment.comment1;
        QCommentLike commentLike = QCommentLike.commentLike;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return jpaQueryFactory
                .select(comment)
                .from(commentLike)
                .join(commentLike.comment, comment)
                .where(commentLike.user.id.eq(user.getId()))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
