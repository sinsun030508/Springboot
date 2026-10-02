package com.mysite.sbb1.question.repository;

import com.mysite.sbb1.question.entity.Question;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionRepositoryTest {

    @Autowired  //에너테이션을 통해 스프링 의존성 주입기능 사용
    private QuestionRepository questionRepository;//di 디펜던지 인덱서 자동 객체 생성

    @Test
    void test(){
        System.out.println("=========== 스프링부트 테스트 연습.");
    }

    //@Test
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

    @Test
    void testFindAll(){
        List<Question> questionList = questionRepository.findAll();
        //System.out.println("============= questionList : " + questionList);
        assertEquals(2, questionList.size());

    }//find by id 5주차

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
    @Test
    void testFindBySubject(){
        Question question = questionRepository.findBySubject("sbb가 무엇인가요?");
        assertEquals(1, question.getId());

    }
    //findBySubjectAndContent 메서드
    @Test
    void testfindBySubjectAndContent(){
        Question question = questionRepository.findBySubjectAndContent("테스트 제목 입니다.", "테스트 입력 내용 입니다.")
                .orElseThrow(() -> new EntityNotFoundException("없어요!!"));
        assertEquals(2, question.getId());

    }
//findBySubjectLike
    @Test
    void testfindBySubjectLike(){
        List<Question> questionList = questionRepository.findBySubjectLike("%sbb%");
        assertEquals(1, questionList.size());
    }

    @Test
    void testUpdate(){
        Question question = questionRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("없음!!"));

        question.setSubject("수정된 제목");

        Question saved = questionRepository.save(question);
        System.out.println("저장 후 : " + saved);
    }
}