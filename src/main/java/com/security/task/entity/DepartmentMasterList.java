//package com.security.task.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.DynamicUpdate;
//
//@Getter
//@Setter
//@Entity
//@DynamicInsert
//@DynamicUpdate
//@Table(name = "DEPT_MASTER_LIST")
//@NoArgsConstructor
//public class DepartmentMasterList {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPARTMENT_SEQ_GEN")
//    @SequenceGenerator(name = "DEPARTMENT_SEQ_GEN", sequenceName = "TESTUSER.SEQ_DEPT_MASTER_SERIAL", allocationSize = 1)
//    @Column(name = "DEPT_MST_ID")
//    private Long deptMstId;
//    @Column(name = "DEPT_MST_CODE")
//    private Integer deptMstCode;
//}
