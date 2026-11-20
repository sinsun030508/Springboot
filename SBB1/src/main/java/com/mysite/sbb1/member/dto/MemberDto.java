package com.mysite.sbb1.member.dto;


import com.mysite.sbb1.member.constant.Department;
import com.mysite.sbb1.member.constant.Gender;
import groovy.transform.builder.Builder;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    @Size(min = 3, max = 30, message = "사용자ID는 3~30자로 입력하세요.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "성별을 선택하세요.")
    private Gender gender;

    @NotNull(message = "학과를 선택하세요.")
    private Department department;

    // 등록 확인(체크박스): 미체크 시 null 이므로 Boolean 권장
    @AssertTrue(message = "등록(이용) 확인에 동의해야 가입할 수 있습니다.")
    private Boolean registration;
}