package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request){
//        AuthenticationResponse response = authenticationService.authenticate(request);
//        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
//                .status("success")
//                .data(response)
//                .message("Đăng nhập thành công")
//                .build());
        var result = authenticationService.authenticate(request);
                return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                .status("success")
                .data(result)
                .message("Đăng nhập thành công")
                .build());
    }

    @PostMapping("/introspect")
    ResponseEntity<ResponseWrapper<IntrospectResponse>> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ResponseEntity.ok(ResponseWrapper.<IntrospectResponse>builder()
                .data(result)
                .build());
    }
}
