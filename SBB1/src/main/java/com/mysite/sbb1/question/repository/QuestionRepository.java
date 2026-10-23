package com.mysite.sbb1.question.repository;

import com.mysite.sbb1.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findBySubject(String subject);
    Optional<Question> findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String keyword);
    Page<Question> findAll(Pageable pageable);
}

