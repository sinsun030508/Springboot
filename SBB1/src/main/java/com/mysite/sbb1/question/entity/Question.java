package com.mysite.sbb1.question.entity;

import com.mysite.sbb1.answer.entity.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity //테이블을 만들음
@Getter
@Setter
@ToString(exclude = "answerList")
@NoArgsConstructor  //기본 생성자
@AllArgsConstructor //전체 생성자
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 200, nullable = false)
    private String subject; //질문 제목

    @Column(columnDefinition = "TEXT")
    private String content; //질문 내용

    private LocalDateTime created; //생성일

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;



}
