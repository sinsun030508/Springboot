package com.mysite.sbb1.question.service;

import com.mysite.sbb1.member.entity.Member;
import com.mysite.sbb1.question.dto.QuestionDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page) {

//        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("created")));
        Page<Question> paging = questionRepository.findAll(pageable);
        return paging;
    }

    public Question getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 엔티티 없음!"));
        return question;
    }

    public void createQuestion(String subject, String content) {
        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .build();

        questionRepository.save(question);
    }

    public void createQuestion(QuestionDto questionDto, Member author) {
        Question question = Question.builder()
                .subject(questionDto.getSubject())
                .content(questionDto.getContent())
                .author(author) // 질문 작성자 설정(추가)
                .build();

        questionRepository.save(question);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }


}