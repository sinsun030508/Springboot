package com.mysite.sbb1.answer.repository;

import com.mysite.sbb1.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * answer 테이블에 접근하는 리포지토리
 * - JpaRepository를 상속받아 save(), findById(), findAll() 등 기본 기능을 그대로 사용
 */
public interface AnswerRespository extends JpaRepository<Answer, Long>{
}
