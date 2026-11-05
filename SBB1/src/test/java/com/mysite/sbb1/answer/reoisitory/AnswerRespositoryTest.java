package com.mysite.sbb1.answer.reoisitory;




import com.mysite.sbb1.answer.entity.Answer;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.repository.QuestionRepository;
import com.mysite.sbb1.question.service.QuestionService;
import com.mysite.sbb1.answer.repository.AnswerRespository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 답변 리포지토리 테스트 (MySQL DB에 실제로 저장/조회함)
 * - @SpringBootTest : 실제 애플리케이션과 똑같이 스프링을 띄워서 테스트
 * - DB에 들어 있는 데이터에 따라 결과가 달라지므로 순서대로 하나씩 실행해야 함
 */
@SpringBootTest
public class AnswerRespositoryTest {

    @Autowired
    private AnswerRespository answerRespository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;

    // 1번 질문에 답변을 하나 저장하고, 저장된 답변의 id가 1인지 확인
    @Test
    void testFindById() {
        Optional<Question> oq = questionRepository.findById(1L);
        assertTrue(oq.isPresent());
        Question question = oq.get();

        Answer answer = new Answer();
        answer.setContent("네 자동으로 생성됩니다.");
        answer.setQuestion(question);
        answer.setCreated(LocalDateTime.now());
        Answer saved = answerRespository.save(answer);
        assertEquals(saved.getId(), 1L);
    }

    /**
     * 1번 질문에 달린 답변 목록을 조회해서 답변이 1개인지 확인
     * - answerList는 LAZY라서 실제로 사용할 때 DB를 조회하는데,
     *   @Transactional이 없으면 조회 시점에 DB 연결이 끊겨 있어서 에러가 남
     */
    @Transactional
    @Test
    void testGetAnswerList() {
        Optional<Question> oq = questionRepository.findById(1L);
        assertTrue(oq.isPresent());
        Question question = oq.get();

        List<Answer> answerList = question.getAnswerList();

        assertEquals(answerList.size(), 1);

        assertEquals(1, question.getAnswerList().size());
    }
}