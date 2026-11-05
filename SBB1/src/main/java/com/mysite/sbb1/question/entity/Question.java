package com.mysite.sbb1.question.entity;

import com.mysite.sbb1.answer.entity.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 엔티티 → DB의 question 테이블과 연결되는 클래스
 * - 필드 하나가 테이블의 컬럼 하나가 됨
 * - application.yml의 ddl-auto: update 설정 때문에 실행하면 테이블이 자동으로 만들어짐
 */
@Entity //테이블을 만들음
@Getter
@Setter
@ToString(exclude = "answerList") // answerList를 빼지 않으면 Question ↔ Answer가 서로 toString을 호출해서 무한 반복됨
@NoArgsConstructor  //기본 생성자
@AllArgsConstructor //전체 생성자
@Builder            // Question.builder().subject(..).build() 형태로 객체를 만들 수 있게 해줌
public class Question {

    // 기본키(PK). IDENTITY : DB의 auto_increment로 1, 2, 3... 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 200, nullable = false) // 최대 200자, 빈 값(null) 저장 불가
    private String subject; //질문 제목

    @Column(columnDefinition = "TEXT")      // 글자 수 제한 없는 TEXT 타입
    private String content; //질문 내용

    private LocalDateTime created; //생성일

    /**
     * 이 질문에 달린 답변 목록 (1:N 관계, 질문 1개 - 답변 여러 개)
     * - mappedBy = "question" : 외래키는 Answer 쪽의 question 필드가 관리함
     * - cascade = ALL : 질문을 저장/삭제하면 답변도 같이 저장/삭제됨
     * - orphanRemoval = true : 목록에서 빠진 답변은 DB에서도 삭제됨
     * - LAZY : 실제로 answerList를 사용할 때 DB에서 조회함
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;



}
