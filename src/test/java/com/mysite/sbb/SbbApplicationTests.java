package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//테스트 환경에서는 기본적으로 repository를 통한 통신만 가능
@SpringBootTest
class SbbApplicationTests {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionService questionService;

    @BeforeEach // 아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
    void beforeEach() {
        // 모든 데이터 삭제
        answerRepository.deleteAll();
        answerRepository.clearAutoIncrement();

        // 모든 데이터 삭제
        questionRepository.deleteAll();

        // 흔적삭제(다음번 INSERT 때 id가 1번으로 설정되도록)
        questionRepository.clearAutoIncrement();

        // 질문 1개 생성
        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        questionRepository.save(q1);  // 첫번째 질문 저장

        // 질문 1개 생성
        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());
        questionRepository.save(q2);  // 두번째 질문 저장



        // 답변 1개 생성
        Answer a1 = new Answer();
        a1.setContent("네 자동으로 생성됩니다.");
        q2.addAnswer(a1);
        a1.setCreateDate(LocalDateTime.now());
        answerRepository.save(a1);
    }

    @Test
    @DisplayName("데이터 저장하기")
    void t001() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content);
        }

        // 질문 1개 생성
//        Question q = new Question();
//        q.setSubject("세계에서 가장 부유한 국가가 어디인가요?");
//        q.setContent("알고 싶습니다.");
//        q.setCreateDate(LocalDateTime.now());
//        questionRepository.save(q);

//        assertEquals("세계에서 가장 부유한 국가가 어디인가요?", questionRepository.findById(3).get().getSubject());

//        Question q1 = new Question();
//        q1.setSubject("sbb가 무엇인가요?");
//        q1.setContent("sbb에 대해서 알고 싶습니다.");
//        q1.setCreateDate(LocalDateTime.now());
//        questionRepository.save(q1);  // 첫번째 질문 저장
//
//        // 질문 1개 생성
//        Question q2 = new Question();
//        q2.setSubject("스프링부트 모델 질문입니다.");
//        q2.setContent("id는 자동으로 생성되나요?");
//        q2.setCreateDate(LocalDateTime.now());
//        questionRepository.save(q2);  // 두번째 질문 저장

    }

    @Test
    @DisplayName("데이터 조회하기_findAll")
    void t002(){
        List<Question> all = this.questionRepository.findAll();
        assertEquals(302, all.size());

        Question q = all.get(0);
        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    @DisplayName("데이터 조회하기_findById")
    void t003(){
        Optional<Question> oq = this.questionRepository.findById(1);
        if(oq.isPresent()){
            Question q = oq.get();
            assertEquals("sbb가 무엇인가요?", q.getSubject());
        }
    }

    @Test
    @DisplayName("데이터 조회하기_findBySubject")
    void t004(){
        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findBySubjectAndContent")
    void t005(){
        Question q = this.questionRepository.findBySubjectAndContent(
                "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        assertEquals(1, q.getId());
    }

    @Test
    @DisplayName("findBySubjectLike")
    void t006(){
        List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
        Question q = qList.get(0);
        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    @DisplayName("데이터 수정하기")
    void t007(){
        Optional<Question> oq = this.questionRepository.findById(1);
        assertTrue(oq.isPresent());
        Question q = oq.get();
        q.setSubject("수정된 제목");
        this.questionRepository.save(q);
    }

    /*
    SQL
    DELETE
    FROM
        question
    WHERE
        id = ?
    */
    @Test
    @DisplayName("데이터 삭제하기")
    void t008() {
        // questionRepository.count()
        // SQL : SELECT COUNT(*) FROM question;
        assertEquals(302, questionRepository.count());
        Optional<Question> oq = questionRepository.findById(1);
        assertTrue(oq.isPresent());
        Question q = oq.get();
        questionRepository.delete(q);
        assertEquals(301, questionRepository.count());
    }

    @Test
    @DisplayName("답변 데이터 생성 후 저장하기")
    void t009() {
        Optional<Question> oq = questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        /*
        // v1
        Optional<Question> oq = questionRepository.findById(2);
        Question q = oq.get();
        */

        /*
        // v2
        Question q = questionRepository.findById(2).get();
        */

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());
        answerRepository.save(a);
    }

    @Test
    @DisplayName("답변 조회하기")
    void t010() {
        Optional<Answer> oa = answerRepository.findById(1);
        assertTrue(oa.isPresent());
        Answer a = oa.get();
        assertEquals(2, a.getQuestion().getId());
    }

    @Transactional
    @Rollback(false)
    @Test
    @DisplayName("질문에 달린 답변 찾기")
    void t011() {
        Optional<Question> oq = questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
    }
}