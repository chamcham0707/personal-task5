package com.sparta.ottoon.follow.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.auth.service.UserService;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.follow.entity.Follow;
import com.sparta.ottoon.follow.filter.FollowerSearchCond;
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
    private static final int PAGE_SIZE= 5;

    private final FollowRepository followRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponseDto followUser(Long followedId, String username) {
        User followUser = findByUsername(username);

        if (followedId == followUser.getId()) {
            throw new CustomException(ErrorCode.NOT_SELF_FOLLOW);
        }

        User followedUser = userService.findById(followedId);
        Follow newFollow = new Follow(followUser, followedId);

        if (returnFollow(followUser, followedId) != null) {
            throw new CustomException(ErrorCode.BAD_FOLLOW);
        }

        followedUser.increaseFollower();
        followRepository.save(newFollow);

        return new ProfileResponseDto(followedUser);
    }

    @Transactional
    public ProfileResponseDto followCancel(Long followedId, String username) {
        User followUser = findByUsername(username);
        User followedUser = userService.findById(followedId);

        Follow cancelFollow = returnFollow(followUser, followedId);

        if (cancelFollow == null) {
            throw new CustomException(ErrorCode.NOT_FOLLOW);
        }

        followedUser.decreaseFollower();
        followRepository.delete(cancelFollow);

        return new ProfileResponseDto(followedUser);
    }

    public List<PostResponseDto> getFollow(String username, int pageNumber, String sortBy, String authorName) {
        List<Post> postList;
        if (authorName != null) {
            postList = authorNameSearchCondition(authorName, pageNumber);
        } else {
            postList = defaultOrWriterSort(username, sortBy, pageNumber);
        }

        return postList.stream().map(f -> PostResponseDto.toDto("성공적으로 조회하였습니다.", 200, f)).toList();
    }

    public List<ProfileResponseDto> getTopTen() {
        List<User> topTen = followRepository.findTopTen();

        return topTen.stream().map(ProfileResponseDto::new).toList();
    }

    private List<Post> authorNameSearchCondition(String authorName, int pageNumber) {
        FollowerSearchCond cond = new FollowerSearchCond(authorName);
        return followRepository.findByCondition(cond, pageNumber, PAGE_SIZE);
    }

    private List<Post> defaultOrWriterSort(String username, String sortBy, int pageNumber) {
        User user = findByUsername(username);

        PathBuilder<Post> postPath = new PathBuilder<>(Post.class, "post");
        OrderSpecifier<?> orderSpecifier = postPath.getDateTime("createdAt", java.util.Date.class).desc();
        if (Objects.equals(sortBy, "writerName")) {
            orderSpecifier = postPath.get("user").getString("username").asc();
        }

        return followRepository.findByAllFollowPostList(user, pageNumber, PAGE_SIZE, orderSpecifier);
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Follow returnFollow(User followUser, Long followedId) {
        Optional<Follow> curFollow = followRepository.findByFollowUserAndFollowedUserId(followUser, followedId);
        if (curFollow.isPresent()) {
            return curFollow.get();
        }
        return null;
    }
}
