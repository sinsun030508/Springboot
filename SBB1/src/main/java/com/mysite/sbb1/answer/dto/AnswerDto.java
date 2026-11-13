package com.mysite.sbb1.answer.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 상세 화면(detail.html)의 답변 등록 폼에서 입력한 값을 담는 DTO
 * - 컨트롤러에서 @Valid를 붙이면 내용이 비어 있는지 검증함
 */
@Data   //getter,setter등등 기능 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDto {

    @NotEmpty(message = "내용은 필수 항목입니다.")  // 빈 답변은 등록할 수 없음
    private String content;

}
