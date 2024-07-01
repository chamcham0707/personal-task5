package com.sparta.ottoon.follow.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.auth.service.UserService;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.follow.entity.Follow;
import com.sparta.ottoon.follow.repository.FollowRepository;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponseDto followUser(long followedId, String username) {
        User followUser = findByUsername(username);

        if (followedId == followUser.getId()) {
            throw new CustomException(ErrorCode.NOT_SELF_FOLLOW);
        }

        User followedUser = userService.findById(followedId);
        Follow newFollow = new Follow(followUser, followedUser.getId());

        if (isFollow(followUser, followedUser)) {
            throw new CustomException(ErrorCode.BAD_FOLLOW);
        }

        followedUser.increaseFollower();
        followRepository.save(newFollow);

        return new ProfileResponseDto(followedUser);
    }

    @Transactional
    public ProfileResponseDto followCancel(Long followId, String username) {
        User followUser = findByUsername(username);
        User followedUser = userService.findById(followId);

        Follow cancelFollow = followRepository.findByFollowUserAndFollowedUserId(followUser, followedUser.getId()).orElseThrow(() ->
                new CustomException(ErrorCode.FAIL_FIND_USER));

        if (!isFollow(followUser, followedUser)) {
            throw new CustomException(ErrorCode.NOT_FOLLOW);
        }

        followedUser.decreaseFollower();
        followRepository.delete(cancelFollow);

        return new ProfileResponseDto(followedUser);
    }

    public List<PostResponseDto> getFollow(String username, int pageNumber, String sortBy) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.FAIL_FIND_USER)
        );

        PathBuilder<Post> postPath = new PathBuilder<>(Post.class, "post");
        OrderSpecifier<?> orderSpecifier = postPath.getDateTime("createdAt", java.util.Date.class).desc();
        if (Objects.equals(sortBy, "writerName")) {
            orderSpecifier = postPath.get("user").getString("username").asc();
        }

        List<Post> followPostList = followRepository.findByAllFollowPostList(user, pageNumber, 5, orderSpecifier);

        return followPostList.stream().map(f -> PostResponseDto.toDto("성공적으로 조회하였습니다.", 200, f)).toList();
    }

    public List<ProfileResponseDto> getTopTen() {
        List<User> topTen = followRepository.findTopTen();

        return topTen.stream().map(ProfileResponseDto::new).toList();
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private boolean isFollow(User followUser, User followedUser) {
        Optional<Follow> curFollow = followRepository.findByFollowUserAndFollowedUserId(followUser, followedUser.getId());
        if (curFollow.isPresent()) {
            return true;
        }
        return false;
    }
}
