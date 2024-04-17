package com.security.task.service.interfaces;

import com.security.task.auth.AuthenticationResponse;
import com.security.task.dtos.UsersInfoDto;
import com.security.task.projections.UserInfoProjection;

import java.util.Map;

public interface UserInfoService {
    AuthenticationResponse save(UsersInfoDto dto);
    Map<String, Object> getUserInfoDetailsList(String userName, Integer page, Integer size, String sortBy);
    UserInfoProjection showUserInfoDataById(Long id);
    void deleteUserInfo(Long id);
}
