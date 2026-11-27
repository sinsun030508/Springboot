package com.mysite.sbb1.answer.service;


import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.answer.entity.Answer;
import com.mysite.sbb1.answer.repository.AnswerRespository;
import com.mysite.sbb1.member.entity.Member;
import com.mysite.sbb1.question.entity.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 답변 관련 비즈니스 로직을 처리하는 서비스 (DB 저장은 AnswerRespository를 통해 처리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {

    private final AnswerRespository answerRespository;

    /**
     * 답변 등록
     * - 어떤 질문에 달 답변인지(question)와 입력한 내용(answerDto)으로 Answer 엔티티를 만들어 저장
     * - 작성일(created)은 저장하는 현재 시간
     */
    public void createAnswer(Question question, AnswerDto answerDto, Member author) {
        Answer answer = Answer.builder()
                .content(answerDto.getContent())
                .question(question)
                .author(author)
                .build();

        Answer saved =  answerRespository.save(answer);
        log.info("======= saved : {}", saved);
    }
    public Answer getAnswer(Long id) {
        Answer answer = answerRespository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다: " + id));
        return answer;
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answerRespository.save(answer);
    }

    public void delete(Answer answer) {
        answerRespository.delete(answer);
    }
}
