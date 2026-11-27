package com.mysite.sbb1.answer.controller;

import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.answer.entity.Answer;
import com.mysite.sbb1.answer.service.AnswerService;
import com.mysite.sbb1.member.entity.Member;
import com.mysite.sbb1.member.servuce.MemberService;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
@Slf4j
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable("id") Long id,
                               @Valid AnswerDto answerDto,
                               BindingResult bindingResult,
                               Principal principal, // 현재 로그인한 사용자 정보(추가)
                               Model model ){
        log.info("=====> id : {}, answerDto : {}", id, answerDto);

        Question question = questionService.getQuestion(id);

        // 답변 작성자 정보 추가를 위해
        Member author = memberService.getMember(principal.getName()); // 현재 로그인한 사용자의 Member 엔티티 조회

        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "question/detail";
        }

        answerService.createAnswer(question, answerDto, author);

        return "redirect:/question/" + question.getId();
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @GetMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Long id,
                               @ModelAttribute("answerDto") AnswerDto answerDto, // 폼에 자동 전달할 DTO
                               Principal principal) { // 현재 로그인한 사용자 정보

        Answer answer = answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }
        answerDto.setContent(answer.getContent());
        // model.addAttribute("answerFormDto", answerFormDto); // 이전에는 이렇게 처리했지만 지금은 저 값을 자동으로 전달
        return "answer/modifyForm"; // 답변 수정 폼으로 이동
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @PostMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Long id,
                               @Valid AnswerDto answerDto, // 폼에 자동 전달할 DTO
                               BindingResult bindingResult,
                               Principal principal) { // 현재 로그인한 사용자 정보

        Answer answer = answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }
        answerService.modify(answer, answerDto.getContent());
        return "redirect:/question/" +  answer.getQuestion().getId();
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    @GetMapping("/delete/{id}")
    public String deleteAnswer(@PathVariable("id") Long id, Principal principal) {
        Answer answer = answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        answerService.delete(answer);

        return "redirect:/question/" + answer.getQuestion().getId();
    }
}