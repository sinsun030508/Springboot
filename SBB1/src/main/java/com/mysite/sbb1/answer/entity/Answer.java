package com.mysite.sbb1.answer.entity;

import com.mysite.sbb1.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor //전체 생성자
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String countent;

    private LocalDateTime created;

    //FetchType fetch() default FetchType.EAGER; 조인
    @ManyToOne(fetch = FetchType.LAZY)  //디폴트는 LAZY  M:1관계
    @JoinColumn(name = "question_id", nullable = false) //포린키
    private Question question;


}
