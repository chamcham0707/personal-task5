package com.sparta.ottoon.follow.service;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.follow.entity.Follow;
import com.sparta.ottoon.follow.repository.FollowRepository;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.repository.PostRepository;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FollowSearchTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowService followService;

    static User testUser;
    static ArrayList<User> followedUsers = new ArrayList<>();

    @BeforeAll
    static void setUp(@Autowired UserRepository userRepository, @Autowired FollowRepository followRepository, @Autowired PostRepository postRepository) {
        testUser = new User("testUser", "test", "test1234!", "test@email.com", UserStatus.ACTIVE);
        followedUsers.add(new User("followed1", "testUser", "test1234!", "followed1@email.com", UserStatus.ACTIVE));
        followedUsers.add(new User("followed2", "testUser", "test1234!", "followed2@email.com", UserStatus.ACTIVE));
        followedUsers.add(new User("followed3", "testUser", "test1234!", "followed3@email.com", UserStatus.ACTIVE));

        for (int i = 0; i < 3; ++i) {
            followedUsers.get(i % 3).increaseFollower();
            userRepository.save(followedUsers.get(i));
        }
        userRepository.save(testUser);

        for (int i = 0; i < 3; ++i) {
            Follow follow = new Follow(testUser, (long) (i % 3) + 1);
            followRepository.save(follow);
            Post post = new Post("this is post", followedUsers.get(i % 3));
            postRepository.save(post);
            followedUsers.get(i % 3).updatePost(post);
        }
    }

    @Test
    @DisplayName("팔로우 게시물 작성 시간순 조회")
    void success1() {
        int pageNumber = 0;
        String sortBy = null;
        String authorName = null;

        List<PostResponseDto> result = followService.getFollow(testUser.getUsername(), pageNumber, sortBy, authorName);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(3L, result.get(0).getPostId());
        Assertions.assertEquals(2L, result.get(1).getPostId());
        Assertions.assertEquals(1L, result.get(2).getPostId());
    }

    @Test
    @DisplayName("팔로우 게시물 이름순 조회")
    void success2() {
        int pageNumber = 0;
        String sortBy = "writerName";
        String authorName = null;

        List<PostResponseDto> result = followService.getFollow(testUser.getUsername(), pageNumber, sortBy, authorName);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1L, result.get(0).getPostId());
        Assertions.assertEquals(2L, result.get(1).getPostId());
        Assertions.assertEquals(3L, result.get(2).getPostId());
    }

    @Test
    @DisplayName("팔로우 게시물 작성자명 필터")
    void success3() {
        int pageNumber = 0;
        String sortBy = null;
        String authorName = "followed2";

        List<PostResponseDto> result = followService.getFollow(testUser.getUsername(), pageNumber, sortBy, authorName);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(2L, result.get(0).getPostId());
    }

    @Test
    @DisplayName("팔로우 TOP 10 조회")
    void success4() {
        List<User> testUsers = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            User user = new User("toptentest" + i, "test", "test1234!", "test@email.com", UserStatus.ACTIVE);
            testUsers.add(user);
            if (i > 2) {
                user.increaseFollower();
            }
            userRepository.save(user);
        }

        List<ProfileResponseDto> result = followService.getTopTen();

        Assertions.assertEquals(10, result.size());
        Assertions.assertEquals(false, result.contains(testUsers.get(0)));
        Assertions.assertEquals(false, result.contains(testUsers.get(1)));
        Assertions.assertEquals(false, result.contains(testUsers.get(2)));
    }
}
