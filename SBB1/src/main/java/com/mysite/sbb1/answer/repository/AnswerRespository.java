package com.mysite.sbb1.answer.repository;

import com.mysite.sbb1.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRespository extends JpaRepository<Answer, Long>{
}
