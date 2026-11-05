package com.mysite.sbb1.question.service;

import com.mysite.sbb1.question.dto.QuestionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 질문 관련 비즈니스 로직을 처리하는 서비스
 * - 컨트롤러 → 서비스 → 리포지토리 → DB 순서로 호출됨
 * - 컨트롤러는 요청/응답만 담당하고, 실제 처리(조회, 저장 등)는 서비스가 담당
 * - @RequiredArgsConstructor : final 필드를 받는 생성자를 자동으로 만들어서
 *   스프링이 QuestionRepository를 주입(DI)해 줌
 */
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 질문 목록을 페이지 단위로 조회 (질문 목록 화면에서 사용)
     * @param page 조회할 페이지 번호 (0부터 시작)
     * @return 한 페이지에 10개씩, 작성일(created) 최신순으로 정렬된 질문 목록
     */
    public Page<Question> getList(int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("created").descending());
        //Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "created");
        Page<Question> paging = questionRepository.findAll(pageable);
        return paging;  // alt + shift + i (inline variable)로 간략화 가능
    }

    /**
     * 질문 등록 : 폼에서 받은 QuestionDto로 Question 엔티티를 만들어 DB에 저장
     * 작성일(created)은 저장하는 현재 시간으로 넣음
     */
    public void createeQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .subject(questionDto.getSubject())
                .content(questionDto.getContent())
                .created(LocalDateTime.now())
                .build();
        questionRepository.save(question);
    }

    /**
     * id로 질문 1개 조회 (질문 상세 화면, 답변 등록에서 사용)
     * 해당 id의 질문이 없으면 EntityNotFoundException 예외 발생
     */
    public Question getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터 없음"));

        return question;




        // 간략한 표현
//    Question question = questionRepository.findById(id)
//        .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
//    return question;
//    Optional<Question> question = questionRepository.findById(id);
//        if(question.isPresent()) {
//            return question.get();
//        } else {
//            throw new IllegalArgumentException("id에 해당하는 질문이 없습니다 : " + id);
//        }
    }

}
