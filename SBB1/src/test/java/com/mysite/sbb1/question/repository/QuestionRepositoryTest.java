package com.mysite.sbb1.question.repository;

import com.mysite.sbb1.question.dto.QuestionDto;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 질문 리포지토리 / 서비스 테스트 (MySQL DB에 실제로 저장/조회함)
 * - 3주차 JPA 쿼리 메서드 연습용
 * - id, 개수를 검사하는 테스트가 많아서 DB가 비어 있는 상태에서 위에서부터 하나씩 실행해야 함
 */
@SpringBootTest
class QuestionRepositoryTest {

    @Autowired  //에너테이션을 통해 스프링 의존성 주입기능 사용
    private QuestionRepository questionRepository;//di 디펜던지 인덱서 자동 객체 생성

    @Autowired
    private QuestionService questionService;

    // 테스트 환경이 정상 동작하는지 확인하는 연습용 테스트
    @Test
    void test(){
        System.out.println("=========== 스프링부트 테스트 연습.");
    }

    /**
     * 질문 저장 테스트 : 질문 2개를 저장함
     * - q1 : 기본 생성자 + setter 방식으로 객체 생성
     * - q2 : @Builder 방식으로 객체 생성
     * - @Transactional을 붙이면 테스트가 끝난 뒤 저장한 내용이 롤백(취소)됨
     */
    @Test
    //@Transactional //롤백

    void testSave(){
        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해 알고 싶어요.");
        q1.setCreated(LocalDateTime.now()); //setter
        System.out.println("============== q1 : " + q1);
        Question saved = questionRepository.save(q1);
        System.out.println("============== saved : " + saved);

        Question q2 = Question.builder()//기본생성자
                .content("테스트 입력 내용 입니다.")
                .subject("테스트 제목 입니다.")
                .created(LocalDateTime.now())
                .build();
        Question saved1 = questionRepository.save(q2);
        assertEquals("테스트 제목 입니다.", saved1.getSubject());

    }

    // 전체 질문 조회 : 저장된 질문이 2개인지 확인
    @Test
    void testFindAll(){
        List<Question> questionList = questionRepository.findAll();
        //System.out.println("============= questionList : " + questionList);
        assertEquals(2, questionList.size());

    }//find by id 5주차

    /**
     * id로 질문 조회 : 같은 조회를 두 가지 방식으로 연습
     * 1) Optional의 isPresent()로 값이 있는지 직접 확인
     * 2) orElseThrow()로 값이 없으면 바로 예외 발생
     */
    @Test
    void testFindById(){
        Optional<Question> res = questionRepository.findById(1L);
        if(res.isPresent()){
            Question question = res.get();
            assertEquals(1, question.getId());
        } else {
            throw new EntityNotFoundException("해당 데이터가 없어요.");
        }

        Question question = questionRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("해당 자료 없음!!"));
        assertEquals(1, question.getId());
    }
    //findbySubject
    // 제목으로 질문 조회 → 1번 질문인지 확인
    @Test
    void testFindBySubject(){
        Question question = questionRepository.findBySubject("sbb가 무엇인가요?");
        assertEquals(1, question.getId());

    }
    //findBySubjectAndContent 메서드
    // 제목 + 내용으로 질문 조회 → 2번 질문인지 확인
    @Test
    void testfindBySubjectAndContent(){
        Question question = questionRepository.findBySubjectAndContent("테스트 제목 입니다.", "테스트 입력 내용 입니다.")
                .orElseThrow(() -> new EntityNotFoundException("없어요!!"));
        assertEquals(2, question.getId());

    }
//findBySubjectLike
    // 제목에 "sbb"가 들어간 질문 조회 (%는 아무 글자나 와도 된다는 뜻)
    @Test
    void testfindBySubjectLike(){
        List<Question> questionList = questionRepository.findBySubjectLike("%sbb%");
        assertEquals(1, questionList.size());
    }

    // 질문 수정 : 조회한 엔티티의 값을 바꾸고 다시 save()하면 UPDATE 쿼리가 실행됨
    @Test
    void testUpdate(){
        Question question = questionRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("없음!!"));

        question.setSubject("수정된 제목");

        Question saved = questionRepository.save(question);
        System.out.println("저장 후 : " + saved);
    }

    // 페이징 테스트용 데이터 300개 생성 (서비스의 질문 등록 기능 사용)
    @Test
    void testManyData(){
        for (int i = 0; i < 300; i++) {
            QuestionDto questionDto =  QuestionDto.builder()
                    .subject("테스트 데이터[" + i + "]")
                    .content("내용 : [" + i + "]")
                    .build();
            questionService.createeQuestion(questionDto);

        }
    }
}