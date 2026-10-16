package com.mysite.sbb1.answer.service;


import com.mysite.sbb1.answer.entity.Answer;
import com.mysite.sbb1.answer.repository.AnswerRespository;
import com.mysite.sbb1.question.entity.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {

    private final AnswerRespository answerRespository;

    public void createAnswer(Question question, String content) {
        Answer answer = Answer.builder()
                .countent(content)
                .question(question)
                .created(LocalDateTime.now())
                .build();

        Answer saved =  answerRespository.save(answer);
        log.info("======= saved : {}", saved);
    }

}
