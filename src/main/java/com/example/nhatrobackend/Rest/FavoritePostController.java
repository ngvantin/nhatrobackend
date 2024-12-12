package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.FavoritePostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite-posts")
@RequiredArgsConstructor
public class FavoritePostController {
    private final FavoritePostService favoritePostService;
    private final AuthenticationFacade authenticationFacade;

//    @PostMapping("/create/{postUuid}")
//    public ResponseEntity<ResponseWrapper<String>> addFavoritePost(
//            @PathVariable String postUuid) {
//        // Lấy userUuid từ Bearer Token
//        String userUuid = authenticationFacade.getCurrentUserUuid();
//        favoritePostService.addFavoritePost(userUuid,postUuid);
//
//        return  ResponseEntity.ok(ResponseWrapper.<String>builder()
//                .status("success")
//                .message("Yêu thích bài đăng thành công.")
//                .build());
//    }

    @PostMapping("/create/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> addFavoritePost(
            @PathVariable String postUuid,
            HttpServletRequest request) {  // Thêm HttpServletRequest để lấy cookie
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid(request);

        favoritePostService.addFavoritePost(userUuid, postUuid);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Yêu thích bài đăng thành công.")
                .build());
    }

    // Thêm API xóa bài viết yêu thích
    @DeleteMapping("/remove/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> removeFavoritePost(
            @PathVariable String postUuid) {
        String userUuid = authenticationFacade.getCurrentUserUuid();
        favoritePostService.removeFavoritePost(userUuid, postUuid);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Xóa bài đăng yêu thích thành công.")
                .build());
    }


}

