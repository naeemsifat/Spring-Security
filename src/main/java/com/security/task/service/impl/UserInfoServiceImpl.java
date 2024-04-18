package com.security.task.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.task.auth.AuthenticationRequest;
import com.security.task.auth.AuthenticationResponse;
import com.security.task.dtos.ChangePasswordRequestDto;
import com.security.task.dtos.UsersInfoDto;
import com.security.task.entity.Token;
import com.security.task.entity.UsersInfo;
import com.security.task.enums.RecordStatus;
import com.security.task.enums.TokenType;
import com.security.task.projections.UserInfoProjection;
import com.security.task.repositories.TokenRepository;
import com.security.task.repositories.UsersInfoRepository;
import com.security.task.service.interfaces.UserInfoService;
import com.security.task.utils.ServiceHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UsersInfoRepository repository;
//    private final DepartmentMasterListRepository departmentMasterListRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationResponse save(UsersInfoDto dto) {
        UsersInfo usersInfo = dto.to();
//        Optional<DepartmentMasterList> departmentMasterList = departmentMasterListRepository.findByDeptMstCode(203);
//        usersInfo.setDepartmentMasterList(departmentMasterList.get());
        usersInfo.setPassword(passwordEncoder.encode(dto.getPassword()));
        usersInfo.setCompanyCode("abc");
        usersInfo.setDeptMstId("203");
        usersInfo.setRecordStatus(RecordStatus.ACTIVE);
        usersInfo.setCreatedBy(dto.getUserId());
        var savedUser = repository.save(usersInfo);
        var jwtToken = jwtService.generateToken(usersInfo);
        var refreshToken = jwtService.generateRefreshToken(usersInfo);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public Map<String, Object> getUserInfoDetailsList(String userName, Integer page, Integer size, String sortBy) {
        ServiceHelper<UserInfoProjection> serviceHelper = new ServiceHelper<>(UserInfoProjection.class);
        return serviceHelper.getList(
                repository.getUserList(userName, serviceHelper.getPageable(sortBy, page, size)),
                page, size
        );
    }

    @Override
    public UserInfoProjection showUserInfoDataById(Long id) {
        return repository.showUserInfoProjectionById(id);
    }

    @Override
    public void deleteUserInfo(Long id) {
        UsersInfo usersInfo = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User info not found with id: " + id));
        usersInfo.setRecordStatus(RecordStatus.DELETED);
        // Revoke all user tokens
        revokeAllUserTokens(usersInfo);
        repository.save(usersInfo);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserId(),
                        request.getPassword()
                )
        );
        UsersInfo user = repository.findByUserIdAndRecordStatus(request.getUserId(), RecordStatus.ACTIVE)
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(UsersInfo user, String jwtToken) {
        var token = Token.builder()
                .usersInfo(user.getId())
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UsersInfo user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final Integer userId;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userId = Integer.valueOf(jwtService.extractUsername(refreshToken));
        if (userId != null) {
            var user = this.repository.findByUserIdAndRecordStatus(userId, RecordStatus.ACTIVE)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void changePassword(ChangePasswordRequestDto request, Principal connectedUser) {

        var user = (UsersInfo) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

}
