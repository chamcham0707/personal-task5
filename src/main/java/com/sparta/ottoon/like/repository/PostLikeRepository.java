package com.sparta.ottoon.like.repository;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.like.entity.PostLike;
import com.sparta.ottoon.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryCustom {
    Long countByPost(Post post);

    Optional<PostLike> findByIdAndUser(Long id, User user);
}
