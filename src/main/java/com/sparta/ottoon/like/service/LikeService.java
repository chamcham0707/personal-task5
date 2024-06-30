package com.sparta.ottoon.like.service;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.comment.repository.CommentRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.like.entity.CommentLike;
import com.sparta.ottoon.like.entity.PostLike;
import com.sparta.ottoon.like.repository.CommentLikeRepository;
import com.sparta.ottoon.like.repository.PostLikeRepository;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public String postlikeOrUnlike(String username, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findByUsername(username).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(post.getUser().getId() == user.getId()){
            throw new CustomException(ErrorCode.FAIL_LIKESELF);
        }
        Optional<PostLike> existingLike = postLikeRepository.findByIdAndUser(postId, user);
        if (existingLike.isPresent()) {
            post.decreaseLikecount();
            user.decreasePostLikeCount();
            postLikeRepository.delete(existingLike.get());
            return "게시글 좋아요 삭제 완료";
        } else {
            post.increaseLikeCount();
            user.increasePostLikeCount();
            PostLike like = new PostLike(user, post);
            postLikeRepository.save(like);
            return "게시글 좋아요 완료";
        }
    }

    @Transactional
    public String commentlikeOrUnlike(String username,Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(ErrorCode.FAIL_GETCOMMENT));
        User user = userRepository.findByUsername(username).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(comment.getUser().getId() == user.getId()){
            throw new CustomException(ErrorCode.FAIL_COMMENTSELF);
        }
        Optional<CommentLike> existingLike = commentLikeRepository.findByIdAndUser(comment.getId(), user);
        if (existingLike.isPresent()) {
            comment.decreaseLikeCount();
            user.decreaseCommentLikeCount();
            commentLikeRepository.delete(existingLike.get());
            return "댓글 좋아요 삭제 완료";
        } else {
            comment.increaseLikeCount();
            user.increaseCommentLikeCount();
            CommentLike like = new CommentLike(user, comment);
            commentLikeRepository.save(like);
            return "댓글 좋아요 완료";
        }
    }

    public String getLikeComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(ErrorCode.FAIL_GETCOMMENT));
        return "commentid" + comment.getId() + "\n좋아요 :" + comment.getLikeCount();
    }

    public String getLikePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        return "postId" + post.getId() + "\n좋아요 :" + post.getLikeCount();
    }
}
