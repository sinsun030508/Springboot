package com.mysite.sbb1.question.controller;

import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.member.entity.Member;
import com.mysite.sbb1.member.servuce.MemberService;
import com.mysite.sbb1.question.dto.QuestionDto;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue="0") int page) {
        Page<Question> paging = questionService.getList(page);
        log.info("==========> paging : {}", paging);
        model.addAttribute("paging", paging);
        return "question/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         AnswerDto answerDto,
                         Model model) {
        log.info("=====> id : {}", id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }


    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @GetMapping("/create")
    public String createInputForm(QuestionDto questionDto) {
        return "question/inputForm";
    }

//    @PostMapping("/create")
//    public String createQeustion(@RequestParam("subject") String subject,
//                                 @RequestParam("content") String content){
//        log.info("======> subject : {} content : {}", subject, content);
//        questionService.createQuestion(subject, content);
//
//        return "redirect:/question/list";
//    }

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능 --> 스프링 시큐리티 설정 필요
    @PostMapping("/create")
    public String createQeustion(@Valid QuestionDto questionDto,
                                 BindingResult bindingResult,
                                 Principal principal){ // 현재 로그인한 사용자 정보

        log.info("======> questionDto : {} ", questionDto);
        if(bindingResult.hasErrors()){
            return "question/inputForm";
        }

        Member author = memberService.getMember(principal.getName()); // 현재 로그인한 사용자의 Member 엔티티 조회

        questionService.createQuestion(questionDto, author);

        return "redirect:/question/list";
    }

    // 질문 수정을 위해 입력 폼으로 이동
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @GetMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, QuestionDto questionDto, Principal principal) {

        Question question = questionService.getQuestion(id);

        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        // 수정할 내용 설정
        questionDto.setSubject(question.getSubject());
        questionDto.setContent(question.getContent());

        return "question/inputForm";
    }

    // 질문 수정하기
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @PostMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id,
                                 @Valid QuestionDto questionDto,
                                 BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question/inputForm";
        }

        Question question = questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        questionService.modify(question, questionDto.getSubject(), questionDto.getContent());

        return "redirect:/question/" + id;
    }

    // 질문 삭제하기
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id, Principal principal) {

        Question question = questionService.getQuestion(id);

        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }


        questionService.delete(question);

        return "redirect:/";
    }

}