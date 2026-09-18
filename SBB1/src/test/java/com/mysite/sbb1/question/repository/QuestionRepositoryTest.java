package com.mysite.sbb1.question.repository;

import com.mysite.sbb1.question.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionRepositoryTest {

    @Autowired  //에너테이션을 통해 스프링 의존성 주입기능 사용
    private QuestionRepository questionRepository;

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
        System.out.println("============== q1 : " + q1);
        Question saved = questionRepository.save(q1);
        System.out.println("============== saved : " + saved);

        Question q2 = Question.builder()//기본생성자
                .content("테스트 입력 내용 입니다.")
                .subject("테스트 제목 입니다.")
                .build();
        Question saved1 = questionRepository.save(q2);
        assertEquals("테스트 제목 입니다.", saved1.getSubject());

    }

    @Test
    void testFindAll(){
        List<Question> questionList = questionRepository.findAll();
        //System.out.println("============= questionList : " + questionList);
        assertEquals(8, questionList.size());

    }
}