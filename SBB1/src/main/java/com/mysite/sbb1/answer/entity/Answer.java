package com.mysite.sbb1.answer.entity;

import com.mysite.sbb1.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 답변 엔티티 → DB의 answer 테이블과 연결되는 클래스
 * - 답변 하나는 반드시 질문 하나에 속함 (N:1 관계)
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor //전체 생성자
@Builder
public class Answer {

    // 기본키(PK), auto_increment로 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 답변 내용. 예전 필드 이름(countent, 오타)으로 만들어진 DB 컬럼을 그대로 사용하기 위해 name을 지정
    @Column(name = "countent", columnDefinition = "TEXT") // DB 컬럼 이름은 기존 그대로 유지
    private String content;

    private LocalDateTime created; // 답변 작성일

    /**
     * 이 답변이 달린 질문 (N:1 관계, 답변 여러 개 - 질문 1개)
     * - @ManyToOne의 기본값은 EAGER(항상 같이 조회)인데, 필요할 때만 조회하도록 LAZY로 지정
     * - @JoinColumn : answer 테이블에 question_id 외래키(FK) 컬럼을 만들고, 빈 값은 허용하지 않음
     */
    //FetchType fetch() default FetchType.EAGER; 조인
    @ManyToOne(fetch = FetchType.LAZY)  // M:1관계
    @JoinColumn(name = "question_id", nullable = false) //포린키
    private Question question;
}
