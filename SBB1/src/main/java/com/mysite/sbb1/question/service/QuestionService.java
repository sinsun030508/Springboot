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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Question> paging = questionRepository.findAll(pageable);
        return paging;  // alt + shift + i (inline variable)로 간략화 가능
    }

    public void createeQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .subject(questionDto.getSubject())
                .content(questionDto.getContent())
                .created(LocalDateTime.now())
                .build();
        questionRepository.save(question);
    }

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