package com.security.task.controllers;

import com.security.task.dtos.ChangePasswordRequestDto;
import com.security.task.projections.UserInfoProjection;
import com.security.task.service.impl.UserInfoServiceImpl;
import com.security.task.utils.CommonDataHelper;
import com.security.task.utils.PaginatedResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.security.task.utils.ResponseBuilder.*;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user-info")
public class UserInfoController {

    private final UserInfoServiceImpl service;
    private final CommonDataHelper commonDataHelper;

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-info-list")
    public ResponseEntity<JSONObject> getEmployeeGeneralInfoList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy, //To sort the value of the list exm: id,aesc to sort it ascending order
            @RequestParam(value = "userName", defaultValue = "") String userName
    ) {
        commonDataHelper.setPageSize(page, size);

        PaginatedResponse response = new PaginatedResponse();

        Map<String, Object> map = service.getUserInfoDetailsList(userName, page, size, sortBy);

        List<UserInfoProjection> projections = (List<UserInfoProjection>) map.get("lists");

        commonDataHelper.getCommonData(page, size, map, response, projections);

        return ok(paginatedSuccess(response).getJson());
    }

    @GetMapping("/user-info-details/{id}")
    public ResponseEntity<JSONObject> updateRequestDetailsById(@PathVariable Long id) {

        if (id == null || id <= 0)
            return badRequest().body(error(null, "Please provide id").getJson());

        UserInfoProjection response = service.showUserInfoDataById(id);

        return ok(success(response).getJson());
    }

    @DeleteMapping("/user-info/{id}")
    public ResponseEntity<String> deleteUserInfo(@PathVariable Long id) {
        try {
            service.deleteUserInfo(id);
            return ResponseEntity.ok("User info with ID " + id + " deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User info not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting user info with ID: " + id);
        }
    }

}
