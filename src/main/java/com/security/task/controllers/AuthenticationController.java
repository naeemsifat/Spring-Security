package com.security.task.controllers;

import com.security.task.auth.AuthenticationRequest;
import com.security.task.auth.AuthenticationResponse;
import com.security.task.dtos.UsersInfoDto;
import com.security.task.entity.UsersInfo;
import com.security.task.enums.RecordStatus;
import com.security.task.repositories.UsersInfoRepository;
import com.security.task.service.impl.UserInfoServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserInfoServiceImpl service;
    private final UsersInfoRepository repository;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> save(@RequestBody UsersInfoDto dto) {
       Optional<UsersInfo>  userInfo = repository.findByEmailAndRecordStatus(dto.getEmail(), RecordStatus.ACTIVE);
       Optional<UsersInfo>  userInfo2 = repository.findByUserIdAndRecordStatus(dto.getUserId(), RecordStatus.ACTIVE);
        if(userInfo.isPresent()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthenticationResponse("This User mail already registered."));
        }
        if(userInfo2.isPresent()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthenticationResponse("This User Id already registered."));
        }
        service.save(dto);
        return ResponseEntity.ok(new AuthenticationResponse("Registration complete"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        Optional <UsersInfo> userInfo = repository.findByUserIdAndRecordStatus(request.getUserId(), RecordStatus.ACTIVE);
        if (userInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthenticationResponse("User account not found."));
        }
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
