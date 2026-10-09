package com.mysite.sbb1.question.controller;

import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/question/list")
    public String list(Model model) {
        List<Question> list = questionService.getList();
        log.info("====== list : {}", list);
        model.addAttribute("list", list);

        return "question/list";
    }
    @GetMapping("/question/{id}")
    public String  detail(@PathVariable("id") Long id){
        log.info("======== id : " + id);
        return "question/detail";
    }

}
