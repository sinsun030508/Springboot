3주차 jpa쿼리 공부하기 

$:변수


@Autowired  //에너테이션을 통해 스프링 의존성 주입기능 사용
@ToString(exclude = "answerList")
@NoArgsConstructor  //기본 생성자
@AllArgsConstructor //전체 생성자
@GetMapping

th:href="@{/question/{id}(id=${question.id})}"
th:href="@{|/question/${question.id}|}" // 변수와 같이 사용할 시 |사용
<h3 th:text="${question.subject}">제목</h3> <- html 내용 가져오기


AnswerService : db접근

private final QuestionService questionService;
final 중요