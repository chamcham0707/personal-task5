package com.sparta.ottoon.profile.controller;

import com.sparta.ottoon.comment.dto.CommentResponseDto;
import com.sparta.ottoon.post.dto.PostResponseDto;
import com.sparta.ottoon.profile.dto.ProfileRequestDto;
import com.sparta.ottoon.profile.dto.ProfileResponseDto;
import com.sparta.ottoon.profile.dto.UserPwRequestDto;
import com.sparta.ottoon.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile API", description = "Profile API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userName}")
public class ProfileController {

    private final ProfileService userService;

    @Operation(summary = "getUser", description = "프로필 조회 기능입니다.")
    @GetMapping()
    public ResponseEntity<ProfileResponseDto> getUser(@PathVariable String userName){
        return ResponseEntity.ok().body(userService.getUser(userName));
    }

    @Operation(summary = "updateUser", description = "프로필 수정 기능입니다.")
    @PostMapping()
    public ResponseEntity<ProfileResponseDto> updateUser(@PathVariable String userName,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody ProfileRequestDto requestDto){
        return ResponseEntity.ok().body(userService.updateUser(userName, userDetails.getUsername(),requestDto));
    }

    @Operation(summary = "updateUserPassword", description = "비밀번호 변경 기능입니다.")
    @PostMapping("/password")
    public ResponseEntity<String> updateUserPassword(@PathVariable String userName,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody @Valid UserPwRequestDto requestDto){
        userService.updateUserPassword(userName, userDetails.getUsername(), requestDto);
        return ResponseEntity.ok().body("비밀번호가 정상적으로 변경되었습니다.");
    }

    @GetMapping("/postLikeList")
    public ResponseEntity<List<PostResponseDto>> getPostLikeList(@PathVariable String userName,
                                                                 @RequestParam int pageNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getPostLikeList(userName, pageNumber));
    }

    @GetMapping("/commentLikeList")
    public ResponseEntity<List<CommentResponseDto>> getCommentLikeList(@PathVariable String userName,
                                                                       @RequestParam int pageNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getCommentLikeList(userName, pageNumber));
    }
}
