package com.mysite.sbb1.question.controller;

import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.question.dto.QuestionDto;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        Page<Question> paging = questionService.getList(page);
        log.info("====== paging : {}", paging);
        model.addAttribute("paging", paging);

        return "question/list";
    }
    @GetMapping("/{id}")
    public String  detail(@PathVariable("id") Long id, AnswerDto answerDto, Model model) {
        log.info("======== id : " + id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    @GetMapping("/create")
    public String createForm(QuestionDto questionDto) {

        return "question/inputForm";
    }

    /* @PostMapping("/create")
    public String createQuestion(@RequestParam("subject") String subject,
                                 @RequestParam("content") String content, Model model){
        log.info("======== subject : {}, content : {}", subject,content);
        questionService.createeQuestion(subject,content);
        return "redirect:/question/list";
    } */
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionDto questionDto,
                                 BindingResult bindingResult) {

        log.info("======== questionDto  : {}", questionDto);
        if(bindingResult.hasErrors()){
            return "question/inputForm";
        }

        questionService.createeQuestion(questionDto);
        return "redirect:/question/list";
    }
}
