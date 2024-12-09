package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.FavoritePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorite-posts")
@RequiredArgsConstructor
public class FavoritePostController {
    private final FavoritePostService favoritePostService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/create/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> addFavoritePost(
            @PathVariable String postUuid) {
        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để thêm FavoritePost
        favoritePostService.addFavoritePost(userUuid,postUuid);

        // Tạo response với status và message
        return  ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Yêu thích bài đăng thành công.")
                .build());
    }
}

