package com.mysite.sbb1.question.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 200, nullable = false)
    private String subject; //질문 제목

    @Column(columnDefinition = "TEXT")
    private String content; //질문 내용

    private LocalDateTime created; //생성일


}
