package com.mysite.sbb1.question.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   //getter,setter등등 기능 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuestionDto {
    @NotEmpty(message = "제목은 필수 항목입니다.")   //공백제거 null허용 안함
    @Size(max = 150, message = "제목은 150자 이하여야 합니다.")
    private String subject;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
