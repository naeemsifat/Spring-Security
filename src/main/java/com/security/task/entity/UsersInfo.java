package com.security.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "USERS_INFO")
@NoArgsConstructor
public class UsersInfo extends BaseEntity implements UserDetails {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ISEQ$$_134130")
//    @SequenceGenerator(name = "ISEQ$$_134130", sequenceName = "TESTUSER.ISEQ$$_134130", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    private Integer userId;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "DESIGNATION")
    private String designation;
    @Column(name = "USER_PASSWORD", length = 200)
    private String password;
    @Column(name = "COMPANY_CODE", length = 5)
    private String companyCode;
    @Column(name = "DEPT_MST_ID", length = 30)
    private String deptMstId;
    @Column(name = "EMAIL_ID", length = 35)
    private String email;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "DEPT_MST_CODE", referencedColumnName = "DEPT_MST_CODE")
//    private DepartmentMasterList departmentMasterList;

//    @OneToMany( mappedBy = "usersInfo")
//    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
