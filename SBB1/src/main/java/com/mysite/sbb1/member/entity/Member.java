package com.mysite.sbb1.member.entity;


import com.mysite.sbb1.member.constant.Department;
import com.mysite.sbb1.member.constant.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;                // 이름

    private String password;                // 비밀번호

    @Column(unique = true, nullable = false)
    private String email;                   // 이메일

    @Enumerated(EnumType.STRING)//열거형
    @Column(nullable = false, length = 20)
    private Gender gender;                  // 성별

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Department department;          // 학과

    @Column(nullable = false)
    private Boolean registration;           // 등록 확인

    private LocalDateTime created;          // 생성일

}
