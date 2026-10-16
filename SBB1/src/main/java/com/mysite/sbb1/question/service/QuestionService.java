package com.mysite.sbb1.question.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        List<Question> questionList = questionRepository.findAll();
        return questionList;  // alt + shift + i (inline variable)로 간략화 가능
    }

    public void createeQuestion(String subject, String content) {
        Question question = Question.builder()
                .subject(subject)
                .content(content)
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