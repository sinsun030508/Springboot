package com.mysite.sbb1.question.repository;

import com.mysite.sbb1.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * question 테이블에 접근(저장/조회/수정/삭제)하는 리포지토리
 * - JpaRepository<엔티티, PK타입>을 상속하면 save(), findAll(), findById(), delete() 등을
 *   직접 구현하지 않아도 자동으로 사용할 수 있음
 * - 메서드 이름 규칙(findBy + 필드명)만 지키면 스프링이 SQL을 자동으로 만들어 줌
 */
@Repository

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 제목이 정확히 일치하는 질문 1개 조회 → WHERE subject = ?
    Question findBySubject(String subject);

    // 제목과 내용이 모두 일치하는 질문 조회 → WHERE subject = ? AND content = ?
    // 결과가 없을 수도 있으므로 Optional로 감싸서 반환
    Optional<Question> findBySubjectAndContent(String subject, String content);

    // 제목에 특정 문자열이 포함된 질문 목록 조회 → WHERE subject LIKE ?
    // 사용 예: findBySubjectLike("%sbb%")
    List<Question> findBySubjectLike(String keyword);

    // 페이징 조회 : Pageable에 담긴 페이지 번호, 개수, 정렬 조건대로 한 페이지 분량만 조회
    Page<Question> findAll(Pageable pageable);
}

