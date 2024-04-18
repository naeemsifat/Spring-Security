package com.security.task.repositories;

import com.security.task.entity.UsersInfo;
import com.security.task.enums.RecordStatus;
import com.security.task.projections.UserInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersInfoRepository extends JpaRepository<UsersInfo, Long> {
    Optional<UsersInfo> findByEmailAndRecordStatus(String email, RecordStatus recordStatus);
    Optional<UsersInfo> findByUserIdAndRecordStatus(Integer userId, RecordStatus recordStatus);
    @Query(value = """
            SELECT
            	ui.ID AS id,
            	ui.USER_ID AS userId,
            	ui.USER_NAME AS userName,
            	ui.MOBILE_NO AS mobileNo,
            	ui.EMAIL_ID AS emailId,
            	ui.COMPANY_CODE AS companyCode,
            	ui.DEPT_MST_ID AS deptMstId,
            	ui.DESIGNATION AS designation,
            	ui.WORK_LOCATION AS workLocation
            FROM
            	USERS_INFO ui
            WHERE
            	ui.STATUS = 'ACTIVE' AND
            	(:userName IS NULL OR :userName = '' OR LOWER(ui.USER_NAME) LIKE LOWER('%' || :userName || '%'))
                        
            """, nativeQuery = true,
            countQuery = """
                    SELECT
                    COUNT(ui.ID)
                    FROM
            	        USERS_INFO ui
                    WHERE
                        ui.STATUS = 'ACTIVE' AND
                        (:userName IS NULL OR :userName = '' OR LOWER(ui.USER_NAME) LIKE LOWER('%' || :userName || '%'))
                    """

    )
    Page<UserInfoProjection> getUserList(String userName, Pageable pageable);
    @Query(value = """
            SELECT
                ui.ID AS id,
                ui.USER_ID AS userId,
                ui.USER_NAME AS userName,
                ui.MOBILE_NO AS mobileNo,
                ui.EMAIL_ID AS emailId,
                ui.COMPANY_CODE AS companyCode,
                ui.DEPT_MST_ID AS deptMstId,
                ui.DESIGNATION AS designation,
                ui.WORK_LOCATION AS workLocation
            FROM
                USERS_INFO ui
            WHERE
                ui.STATUS = 'ACTIVE'
                AND ui.ID = :id
                        
            """, nativeQuery = true)
    UserInfoProjection showUserInfoProjectionById(Long id);
}
