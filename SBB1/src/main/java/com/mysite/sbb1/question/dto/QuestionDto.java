package com.mysite.sbb1.question.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 등록 폼(inputForm.html)에서 입력한 값을 담아서 전달하는 객체 (DTO: Data Transfer Object)
 * - 엔티티(Question)를 화면에 직접 노출하지 않고, 입력에 필요한 값만 받기 위해 사용
 * - 컨트롤러에서 @Valid를 붙이면 아래 검증 어노테이션이 동작하고,
 *   실패하면 message 내용이 화면에 에러로 표시됨
 */
@Data   //getter,setter등등 기능 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuestionDto {
    @NotEmpty(message = "제목은 필수 항목입니다.")   //null, 빈 문자열("") 허용 안함
    @Size(max = 150, message = "제목은 150자 이하여야 합니다.")  // 최대 길이 검사
    private String subject;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
