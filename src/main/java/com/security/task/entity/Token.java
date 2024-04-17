package com.security.task.entity;

import com.security.task.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TOKEN")
public class Token {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
//    @SequenceGenerator(name = "token_seq", sequenceName = "TOKEN_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

//    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "USER_ID")
    private Long usersInfo;
}
