package com.sa.identity.controller;

import com.sa.common.dto.ApiResponse;
import com.sa.identity.dto.request.AuthenticationRequest;
import com.sa.identity.dto.request.IntrospectRequest;
import com.sa.identity.dto.response.AuthenticationResponse;
import com.sa.identity.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import com.nimbusds.jose.JOSEException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<Boolean> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        Boolean result = authenticationService.introspect(request);
        return ApiResponse.<Boolean>builder()
                .result(result)
                .build();
    }
}
