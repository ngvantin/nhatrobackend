package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.AuthenticationRequest;
import com.example.nhatrobackend.DTO.AuthenticationResponse;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                .status("success")
                .data(response)
                .message("Đăng nhập thành công")
                .build());
    }
}
