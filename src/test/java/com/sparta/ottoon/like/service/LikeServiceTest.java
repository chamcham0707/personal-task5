package com.sparta.ottoon.like.service;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.comment.repository.CommentRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.like.entity.CommentLike;
import com.sparta.ottoon.like.entity.PostLike;
import com.sparta.ottoon.like.repository.CommentLikeRepository;
import com.sparta.ottoon.like.repository.PostLikeRepository;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Nested
    @DisplayName("게시물 좋아요 테스트")
    class PostLikeTest {
        User user;
        Post post;
        Long postId = 1L;

        @BeforeEach
        void setUp() {
            String username = "testUser";
            user = new User(username, "test1", "test1234!", "test@email.com", UserStatus.ACTIVE);
            User mockPostUser = Mockito.mock(User.class);
            post = new Post("this is test post", mockPostUser);
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(postLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.empty());

            likeService.postlikeOrUnlike(username, postId);
        }

        @Test
        @DisplayName("게시물 좋아요 등록 테스트")
        void post_like_test1() {
            likeService.postlikeOrUnlike(user.getUsername(), postId);

            Assertions.assertEquals(2L, post.getLikeCount());
            Assertions.assertEquals(2L, user.getPostLikeCount());
            verify(postLikeRepository, times(2)).save(any(PostLike.class));
        }

        @Test
        @DisplayName("게시물 좋아요 삭제 테스트")
        void post_like_test2() {
            PostLike postLike = new PostLike(user, post);
            when(postLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.of(postLike));

            likeService.postlikeOrUnlike(user.getUsername(), postId);

            Assertions.assertEquals(0L, post.getLikeCount());
            Assertions.assertEquals(0L, user.getPostLikeCount());
            verify(postLikeRepository, times(1)).delete(postLike);
        }

        @Test
        @DisplayName("게시물 좋아요 삭제 테스트 - 예외 발생 테스트")
        void post_like_test3() {
            PostLike postLike = new PostLike(user, post);
            when(postLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.of(postLike));

            likeService.postlikeOrUnlike(user.getUsername(), postId);
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
                likeService.postlikeOrUnlike(user.getUsername(), postId);
            });
            Assertions.assertEquals("Post::likeCount is 0 or under", exception.getMessage());
        }

        @Test
        @DisplayName("자신의 게시물에 좋아요 등록 테스트 - 예외 발생 테스트")
        void post_like_test4() {
            Long mockPostId = 13L;
            String mockUsername = "mockUser";
            Post mockPost = Mockito.mock(Post.class);
            User mockUser = Mockito.mock(User.class);
            when(postRepository.findById(mockPostId)).thenReturn(Optional.of(mockPost));
            when(userRepository.findByUsername(mockUsername)).thenReturn(Optional.of(mockUser));
            when(mockPost.getUser()).thenReturn(mockUser);
            when(mockUser.getId()).thenReturn(1L);

            CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
                likeService.postlikeOrUnlike(mockUsername, mockPostId);
            });
            Assertions.assertEquals("본인 게시글에는 좋아요를 할수없습니다.", exception.getMessage());
        }
    }

    @Nested
    class CommentLikeTest {
        User user;
        Comment comment;
        Long commentId = 1L;

        @BeforeEach
        void setUp() {
            String username = "testUser";
            user = new User(username, "test1", "test1234!", "test@email.com", UserStatus.ACTIVE);
            User mockCommentUser = Mockito.mock(User.class);
            Post mockPost = Mockito.mock(Post.class);
            comment = new Comment("this is test comment", mockCommentUser, mockPost);
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(commentLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.empty());

            likeService.commentlikeOrUnlike(username, commentId);
        }

        @Test
        @DisplayName("댓글 좋아요 등록 테스트")
        void comment_like_test1() {
            likeService.commentlikeOrUnlike(user.getUsername(), commentId);

            Assertions.assertEquals(2L, comment.getLikeCount());
            Assertions.assertEquals(2L, user.getCommentLikeCount());
            verify(commentLikeRepository, times(2)).save(any(CommentLike.class));
        }

        @Test
        @DisplayName("댓글 좋아요 삭제 테스트")
        void comment_like_test2() {
            CommentLike commentLike = new CommentLike(user, comment);
            when(commentLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.of(commentLike));

            likeService.commentlikeOrUnlike(user.getUsername(), commentId);

            Assertions.assertEquals(0L, comment.getLikeCount());
            Assertions.assertEquals(0L, user.getCommentLikeCount());
            verify(commentLikeRepository, times(1)).delete(commentLike);
        }

        @Test
        @DisplayName("댓글 좋아요 삭제 테스트 - 예외 발생 테스트")
        void comment_like_test3() {
            CommentLike commentLike = new CommentLike(user, comment);
            when(commentLikeRepository.findByIdAndUser(null, user)).thenReturn(Optional.of(commentLike));

            likeService.commentlikeOrUnlike(user.getUsername(), commentId);
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
                likeService.commentlikeOrUnlike(user.getUsername(), commentId);
            });
            Assertions.assertEquals("Comment::likeCount is 0 or under", exception.getMessage());
        }

        @Test
        @DisplayName("자신의 댓글에 좋아요 등록 테스트 - 예외 발생 테스트")
        void comment_like_test4() {
            Long mockCommentId = 13L;
            String mockUsername = "mockUser";
            Comment mockComment = Mockito.mock(Comment.class);
            User mockUser = Mockito.mock(User.class);
            when(commentRepository.findById(mockCommentId)).thenReturn(Optional.of(mockComment));
            when(userRepository.findByUsername(mockUsername)).thenReturn(Optional.of(mockUser));
            when(mockComment.getUser()).thenReturn(mockUser);
            when(mockUser.getId()).thenReturn(1L);

            CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
                likeService.commentlikeOrUnlike(mockUsername, mockCommentId);
            });
            Assertions.assertEquals("본인 댓글에는 좋아요를 할수 없습니다.", exception.getMessage());
        }
    }
}