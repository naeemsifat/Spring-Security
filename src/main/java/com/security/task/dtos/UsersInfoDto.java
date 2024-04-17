package com.security.task.dtos;

import com.security.task.entity.UsersInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsersInfoDto {
    private String designation;
    private String password;
    private Integer userId;
    private String email;
    private String userName;
//    private String deptMstCode;

    public UsersInfo to(){
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setDesignation(designation);
//        usersInfo.setPassword(password);
        usersInfo.setEmail(email);
        usersInfo.setUserId(userId);
        usersInfo.setUserName(userName);
        return usersInfo;
    }

    public UsersInfo update(UsersInfo usersInfo){
       // usersInfo.setUserId(userId);
        usersInfo.setDesignation(designation);
//        usersInfo.setPassword(password);
        usersInfo.setEmail(email);
        return usersInfo;
    }
}
