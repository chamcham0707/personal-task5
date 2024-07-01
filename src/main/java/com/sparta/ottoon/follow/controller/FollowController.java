package com.sparta.ottoon.follow.controller;

import com.sparta.ottoon.follow.dto.FollowResponseDto;
import com.sparta.ottoon.follow.service.FollowService;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Follow API", description = "Follow API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows/users")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "createFollow", description = "팔로우 추가 기능입니다.")
    @PostMapping("/{followedId}")
    public ResponseEntity<FollowResponseDto<Void>> followUser(@PathVariable long followedId,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        ProfileResponseDto profileResponseDto = followService.followUser(followedId, userDetails.getUsername());
        return ResponseEntity.ok().body(new FollowResponseDto<>(HttpStatus.OK.value(),
                profileResponseDto.getUsername() + "님을 팔로우 했습니다.",
                null));
    }

    @Operation(summary = "deleteFollow", description = "팔로우 취소 기능입니다.")
    @PutMapping("/{followedId}")
    public ResponseEntity<FollowResponseDto<Void>> followCancel(@PathVariable long followedId,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        ProfileResponseDto profileResponseDto = followService.followCancel(followedId, userDetails.getUsername());
        return ResponseEntity.ok().body(new FollowResponseDto<>(HttpStatus.OK.value(),
                profileResponseDto.getUsername() + "님의 팔로우를 취소 했습니다.",
                null));
    }

    @GetMapping()
    public ResponseEntity<List<PostResponseDto>> getFollow(@RequestParam int pageNumber,
                                                           @RequestParam String sortBy,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getFollow(userDetails.getUsername(), pageNumber, sortBy));
    }
}
