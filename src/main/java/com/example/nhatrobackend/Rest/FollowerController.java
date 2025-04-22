package com.example.nhatrobackend.Rest;


import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowerController {

    private final FollowerService followerService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/{followedUserUuid}/follow")
    public ResponseEntity<ResponseWrapper<String>> followUser(@PathVariable String followedUserUuid) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = followerService.followUser(currentUserId, followedUserUuid);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message(message)
                .build());
    }

    @DeleteMapping("/{followedUserUuid}/unfollow")
    public ResponseEntity<ResponseWrapper<String>> unfollowUser(@PathVariable String followedUserUuid) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = followerService.unfollowUser(currentUserId, followedUserUuid);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message(message)
                .build());
    }

    @GetMapping("/{followedUserUuid}/is-following")
    public ResponseEntity<ResponseWrapper<Boolean>> isFollowing(@PathVariable String followedUserUuid) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        boolean isFollowing = followerService.isFollowing(currentUserId, followedUserUuid);
        return ResponseEntity.ok(ResponseWrapper.<Boolean>builder()
                .status("success")
                .data(isFollowing)
                .build());
    }

    // Các endpoint khác có thể có:
    // - GET /following: Lấy danh sách người mà người dùng hiện tại đang theo dõi
    // - GET /{userId}/followers: Lấy danh sách những người theo dõi một người dùng cụ thể
}
