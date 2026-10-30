3주차 jpa쿼리 공부하기 

$:변수


@Autowired  //에너테이션을 통해 스프링 의존성 주입기능 사용
@ToString(exclude = "answerList")
@NoArgsConstructor  //기본 생성자
@AllArgsConstructor //전체 생성자
@GetMapping

th:href="@{/question/{id}(id=${question.id})}"
th:href="@{|/question/${question.id}|}" // 변수와 같이 사용할 시 |사용
<.h3 th:text="${question.subject}">제목</h3> <- html 내용 가져오기


AnswerService : db접근

private final QuestionService questionService;
final 중요


8주차
Validation 라이브러리 : Notnull등등 기능 제공
DTO사용


9주차
페이징 기능 사용
1. paging.number: 현재 페이지 번호를 의미 부여
2. document.getElementById("id").value = this.dataset.page; // data-page 필드 값을 페이지로 가져옴
3. list.html에 페이징 기능 추가
4. DTO같은 것이 변했을 시 대응, 페이지 수를 줄이거나하는 식의 변환 시험 