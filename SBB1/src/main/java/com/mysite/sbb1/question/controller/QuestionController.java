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

/**
 * 질문 관련 요청(목록, 상세, 등록)을 받아서 처리하는 컨트롤러
 * - @RequestMapping("/question") : 이 클래스의 모든 URL 앞에 /question이 붙음
 * - @Slf4j : log.info(...)로 콘솔에 로그를 찍을 수 있게 해줌
 * - Model : 컨트롤러에서 화면(html)으로 데이터를 넘길 때 사용하는 객체
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * GET /question/list?page=번호  (질문 목록 화면)
     * - page 파라미터가 없으면 0페이지(첫 페이지)를 보여줌
     * - 조회한 페이지 데이터를 "paging"이라는 이름으로 list.html에 넘김
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        Page<Question> paging = questionService.getList(page);
        log.info("====== paging : {}", paging);
        model.addAttribute("paging", paging);

        return "question/list";
    }

    /**
     * GET /question/{id}  (질문 상세 화면)
     * - URL의 {id} 값을 @PathVariable로 받아 해당 질문을 조회해서 detail.html에 넘김
     * - AnswerDto 파라미터 : 상세 화면 아래 답변 등록 폼(th:object="${answerDto}")에서
     *   사용할 빈 객체를 화면에 넘겨주기 위해 필요함
     */
    @GetMapping("/{id}")
    public String  detail(@PathVariable("id") Long id, AnswerDto answerDto, Model model) {
        log.info("======== id : " + id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    /**
     * GET /question/create  (질문 등록 폼 화면)
     * - QuestionDto 파라미터 : inputForm.html의 th:object="${questionDto}"에서 사용할 빈 객체
     */
    @GetMapping("/create")
    public String createForm(QuestionDto questionDto) {

        return "question/inputForm";
    }

    // 7주차 방식 : 폼 값을 @RequestParam으로 하나씩 받던 방법 (8주차에 DTO 방식으로 변경)
    /* @PostMapping("/create")
    public String createQuestion(@RequestParam("subject") String subject,
                                 @RequestParam("content") String content, Model model){
        log.info("======== subject : {}, content : {}", subject,content);
        questionService.createeQuestion(subject,content);
        return "redirect:/question/list";
    } */

    /**
     * POST /question/create  (질문 등록 처리)
     * - @Valid : QuestionDto의 @NotEmpty, @Size 검증을 실행
     * - BindingResult : 검증 결과가 담김. 반드시 @Valid 파라미터 바로 뒤에 와야 함
     * - 검증 실패 → 입력 화면으로 돌아가서 에러 메시지 표시
     * - 검증 성공 → 질문 저장 후 목록 화면으로 이동(redirect)
     */
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
