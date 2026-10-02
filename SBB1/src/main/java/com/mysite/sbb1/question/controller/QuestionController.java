package com.mysite.sbb1.question.controller;

import com.mysite.sbb1.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/question/list")
    public String list(Model model) {

        model.addAttribute(
                "questionList",
                questionService.getList()
        );

        return "question/question_list";
    }
}
