package com.sparta.ottoon.follow.service;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.auth.service.UserService;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.follow.entity.Follow;
import com.sparta.ottoon.follow.repository.FollowRepository;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private FollowRepository followRepository;

    @Nested
    @DisplayName("팔로두 등록 테스트")
    class CreateFollow {

        User mockFollowUser;
        User followedUser;
        Long followedId;
        String followUsername;

        @BeforeEach
        void setUp() {
            mockFollowUser = Mockito.mock(User.class);
            followedUser = new User("followedUser", "testUser", "test1234!", "test@email.com", UserStatus.ACTIVE);
            followedId = 1L;
            followUsername = "testFollowUser";

            when(userRepository.findByUsername(followUsername)).thenReturn(Optional.of(mockFollowUser));
            when(mockFollowUser.getId()).thenReturn(14L);
            when(userService.findById(followedId)).thenReturn(followedUser);
        }

        @Test
        @DisplayName("성공 테스트")
        void success() {
            when(followRepository.findByFollowUserAndFollowedUserId(mockFollowUser, followedId)).thenReturn(Optional.empty());

            ProfileResponseDto result = followService.followUser(followedId, followUsername);

            assertEquals(1, followedUser.getFollower());
            assertEquals(followedUser.getUsername(), result.getUsername());
            assertEquals(followedUser.getEmail(), result.getEmail());
            assertEquals(followedUser.getIntro(), result.getIntro());
            assertEquals(followedUser.getNickname(), result.getNickname());
            assertEquals(followedUser.getPostLikeCount(), result.getPostLikeCount());
            verify(userRepository, times(1)).findByUsername(followUsername);
            verify(mockFollowUser, times(1)).getId();
            verify(userService, times(1)).findById(followedId);
            verify(followRepository, times(1)).findByFollowUserAndFollowedUserId(mockFollowUser, followedId);
            verify(followRepository, times(1)).save(any(Follow.class));
        }

        @Test
        @DisplayName("예외 발생 테스트 - 자기 자신을 팔로우 할 때")
        void error1() {
            when(mockFollowUser.getId()).thenReturn(followedId);

            CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
                followService.followUser(followedId, followUsername);
            });
            assertEquals("자신을 팔로우할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("예외 발생 테스트 - 이미 등록된 팔로우")
        void error2() {
            when(followRepository.findByFollowUserAndFollowedUserId(mockFollowUser, followedId)).thenReturn(Optional.of(new Follow()));

            CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
                followService.followUser(followedId, followUsername);
            });
            assertEquals("이미 팔로우한 사용자입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("팔로우 취소 테스트")
    class FollowCancel {

        User mockFollowUser;
        User followedUser;
        Long followedId;
        String followUsername;

        @BeforeEach
        void setUp() {
            mockFollowUser = Mockito.mock(User.class);
            followedUser = new User("followedUser", "testUser", "test1234!", "test@email.com", UserStatus.ACTIVE);
            followedId = 1L;
            followUsername = "testFollowUser";

            when(userRepository.findByUsername(followUsername)).thenReturn(Optional.of(mockFollowUser));
            when(mockFollowUser.getId()).thenReturn(14L);
            when(userService.findById(followedId)).thenReturn(followedUser);
            when(followRepository.findByFollowUserAndFollowedUserId(mockFollowUser, followedId)).thenReturn(Optional.empty());

            followService.followUser(followedId, followUsername);
        }

        @Test
        @DisplayName("성공 테스트")
        void success() {
            when(followRepository.findByFollowUserAndFollowedUserId(mockFollowUser, followedId)).thenReturn(Optional.of(new Follow()));

            ProfileResponseDto result = followService.followCancel(followedId, followUsername);

            assertEquals(0, followedUser.getFollower());
            assertEquals(followedUser.getUsername(), result.getUsername());
            assertEquals(followedUser.getEmail(), result.getEmail());
            assertEquals(followedUser.getIntro(), result.getIntro());
            assertEquals(followedUser.getNickname(), result.getNickname());
            assertEquals(followedUser.getPostLikeCount(), result.getPostLikeCount());
            verify(userRepository, times(2)).findByUsername(followUsername);
            verify(userService, times(2)).findById(followedId);
            verify(followRepository, times(2)).findByFollowUserAndFollowedUserId(mockFollowUser, followedId);
            verify(followRepository, times(1)).delete(any(Follow.class));
        }

        @Test
        @DisplayName("에러 발생 테스트 - 팔로우 하지 않은 사람일 때")
        void error1() {
            when(followRepository.findByFollowUserAndFollowedUserId(mockFollowUser, followedId)).thenReturn(Optional.empty());

            CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
                followService.followCancel(followedId, followUsername);
            });
            assertEquals("팔로우 되어있지 않은 사용자입니다.", exception.getMessage());
        }
    }
}