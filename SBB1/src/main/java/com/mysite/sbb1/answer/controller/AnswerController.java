package com.mysite.sbb1.answer.controller;

import com.mysite.sbb1.answer.service.AnswerService;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller //컨트롤러 역활 지정
@Slf4j
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    @PostMapping("/answer/create/{id}")//post방식이라
    public String createAnswer(@PathVariable("id") Long id,
                               @RequestParam("content") String content){
        log.info("======= id : {}, content : {}", id, content);
        Question question = questionService.getQuestion(id);
        answerService.createAnswer(question, content);
        return "redirect:/question/" + id;
    }
}
