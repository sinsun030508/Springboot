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


---

# SBB1 처음부터 만들기 (커밋 로그 기준 주차별 가이드)

## 0. 준비물

- JDK 21, IntelliJ IDEA, MySQL 8
- MySQL에 DB 생성: `CREATE DATABASE sbb_c;` (계정 root / 비밀번호 0000)

---

## 1주차 (first commit): 프로젝트 생성

**1) start.spring.io(또는 IntelliJ New Project)에서 아래 설정으로 생성**
- Gradle - Groovy, Java 21, Spring Boot 4.1.x
- Group `com.mysite`, Artifact `SBB1`, Package `com.mysite.sbb1`
- 의존성: Spring Web(webmvc), Thymeleaf, Lombok, (GraalVM Native Support)

**2) 컨트롤러 2개 작성** (`com/mysite/sbb1/common/controller/`)

```java
// StartController.java : JSON 반환 연습 (/start → {"x":10.0,"y":20.0,...})
@RestController
public class StartController {
    @GetMapping("/start")
    public Point start() {          // java.awt.Point
        return new Point(10, 20);
    }
}
```

```java
// indexController.java : 템플릿 반환
@Controller
public class indexController {
    @GetMapping("/")
    public String index() { return "index"; }   // templates/index.html
}
```

**3) `templates/index.html`** 에 `<h1>Spring boot Start!!</h1>` 넣고 실행한 뒤 `http://localhost:8080` 접속.
(`static/index_.html`은 정적 파일 연습용이었고 2주차에 삭제했습니다.)

---

## 2주차: JPA + MySQL 연결, Question 엔티티

**1) `build.gradle` 의존성 추가**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'com.mysql:mysql-connector-j:8.4.0'
```

**2) `application.properties` 삭제 → `application.yml` 생성**
```yaml
server:
  port: 8080
spring:
  application:
    name: SBB
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sbb_c?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: '0000'
  jpa:
    open-in-view: true
    hibernate:
      ddl-auto: update        # create: 매번 지움 / update: 유지
      naming:
        physical-strategy: org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  servlet:
    encoding:
      charset: UTF-8
```

**3) `question/entity/Question.java`**
```java
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime created;
}
```
실행하면 MySQL에 `question` 테이블이 자동으로 생깁니다.

---

## 3주차: Answer 엔티티, Repository, JPA 쿼리 테스트

**1) `build.gradle`**: `developmentOnly 'org.springframework.boot:spring-boot-devtools'` 추가

**2) `answer/entity/Answer.java`** (N:1 관계)
```java
@Entity @Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Builder
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "countent", columnDefinition = "TEXT") // DB 컬럼 이름은 기존 그대로 유지
    private String content;             // ← 원래는 필드 이름도 countent(오타)였음 (아래 주의 참고)

    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
```

**3) Question에 양방향 관계 추가**
```java
@ToString(exclude = "answerList")   // 순환참조 방지
...
@OneToMany(fetch = FetchType.LAZY, mappedBy = "question",
           cascade = CascadeType.ALL, orphanRemoval = true)
private List<Answer> answerList;
```

**4) `question/repository/QuestionRepository.java`**
```java
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findBySubject(String subject);
    Optional<Question> findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String keyword);
}
```

**5) `src/test/.../question/repository/QuestionRepositoryTest.java`**
`@SpringBootTest` + `@Autowired`로 `testSave`, `testFindAll`, `testFindById`, `testFindBySubject`, `testfindBySubjectAndContent`, `testfindBySubjectLike("%sbb%")`, `testUpdate`를 차례로 작성하고 하나씩 실행합니다.

---

## 5주차: Service / Controller 계층, 질문 목록

**1) `build.gradle`**: `implementation 'nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:4.0.1'`

**2) `answer/repository/AnswerRespository.java`**
```java
public interface AnswerRespository extends JpaRepository<Answer, Long> {}
```

**3) `question/service/QuestionService.java`**
```java
@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;   // final + 생성자 주입

    public List<Question> getList() { return questionRepository.findAll(); }
}
```

**4) `question/controller/QuestionController.java`**
```java
@Controller @Slf4j @RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("questionList", questionService.getList());
        return "question/list";
    }
}
```

**5) `templates/question/list.html`** 은 처음에 `th:text` 연습용으로 간단하게 만들었습니다.

**6) `AnswerRespositoryTest.java`**: 1번 질문에 답변을 저장하는 테스트와, `@Transactional`을 붙여 `question.getAnswerList()`를 조회하는 테스트를 작성합니다.

---

## 6주차: Bootstrap + 레이아웃, 질문 상세

**1) 정적 파일**: Bootstrap 5를 내려받아 넣습니다.
- `static/css/bootstrap.min.css`
- `static/js/bootstrap.bundle.min.js`

**2) 레이아웃 구성** (layout-dialect 사용)
- `templates/layouts/layout.html`: `<head>`에 CSS, `header` / `footer` fragment, 본문 자리 `layout:fragment="content"`, 페이지별 영역 `layout:fragment="css"` / `layout:fragment="script"`
- `templates/fragments/header.html`: Bootstrap navbar (`th:fragment="header"`), "질문 목록" 링크 `/question/list`
- `templates/fragments/footer.html`: `th:fragment="footer"`

**3) 각 페이지는 레이아웃을 상속**
```html
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">
<div layout:fragment="content" class="container my-3"> ... </div>
</html>
```
`index.html`과 `temp.html`을 이 방식으로 바꾸고, indexController에 `/temp`를 추가합니다.

**4) 질문 상세**
```java
@GetMapping("/{id}")
public String detail(@PathVariable("id") Long id, Model model) {
    model.addAttribute("question", questionService.getQuestion(id));
    return "question/detail";
}
```
`list.html`은 Bootstrap 테이블로 바꾸고, 제목 링크는 `th:href="@{|/question/${question.id}|}"`로 겁니다.

**5) `logback-spring.xml`**: 콘솔 로그 패턴을 설정하고, `org.springframework` 로그는 error 레벨로 낮춥니다.

---

## 7주차: 질문 등록, 답변 등록

**1) QuestionService**
```java
public Question getQuestion(Long id) {
    return questionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 데이터 없음"));
}
public void createeQuestion(String subject, String content) { ... }   // 7주차는 파라미터 방식
```

**2) QuestionController**: `GET /question/create`는 `question/inputForm`을 반환하고, `POST /question/create`는 `@RequestParam`으로 받아 저장한 뒤 `redirect:/question/list`로 보냅니다.

**3) `answer/service/AnswerService.java`, `answer/controller/AnswerController.java`**
- `POST /answer/create/{id}`: 질문을 조회해서 답변을 저장하고 `redirect:/question/{id}`로 보냅니다.

**4) 템플릿**
- `question/inputForm.html`: 제목/내용 입력 폼
- `question/detail.html`: 질문 카드, 답변 개수(`#lists.size(question.answerList)`), 답변 목록, 답변 등록 폼

---

## 8주차: Validation + DTO

**1) `build.gradle`**: `implementation 'org.springframework.boot:spring-boot-starter-validation'`

**2) DTO 작성**
```java
// question/dto/QuestionDto.java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionDto {
    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 150, message = "제목은 150자 이하여야 합니다.")
    private String subject;
    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
// answer/dto/AnswerDto.java : content 하나 + @NotEmpty
```

**3) 컨트롤러에서 `@Valid` + `BindingResult` 사용**
```java
@GetMapping("/create")
public String createForm(QuestionDto questionDto) { return "question/inputForm"; }

@PostMapping("/create")
public String createQuestion(@Valid QuestionDto questionDto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return "question/inputForm";
    questionService.createeQuestion(questionDto);
    return "redirect:/question/list";
}
```
- 상세 화면에서도 폼이 `answerDto`를 쓰기 때문에 `detail(..., AnswerDto answerDto, Model model)` 파라미터를 추가합니다.
- AnswerController도 `@Valid AnswerDto`로 바꾸고, 에러가 나면 `question`을 model에 담아 `question/detail`로 돌려보냅니다.

**4) 템플릿에 에러 메시지 표시**
```html
<form th:action="@{/question/create}" th:object="${questionDto}" method="post">
  <input th:field="*{subject}" ...>
  <p th:if="${#fields.hasErrors('subject')}" th:errors="*{subject}" class="fledError">error</p>
```

**5) 테스트 `testManyData()`**: `createeQuestion(QuestionDto)`로 질문 300개를 생성합니다. 9주차 페이징 확인용 데이터입니다.

---

## 9주차: 페이징

**1) Repository**: `Page<Question> findAll(Pageable pageable);`

**2) Service**
```java
public Page<Question> getList(int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by("created").descending());
    return questionRepository.findAll(pageable);
}
```

**3) Controller**
```java
@GetMapping("/list")
public String list(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
    model.addAttribute("paging", questionService.getList(page));
    return "question/list";
}
```

**4) `list.html`**
- 반복 대상을 `${paging}`으로 바꿉니다.
- 번호 컬럼: `${paging.getTotalElements - (paging.number * paging.size) - loop.index}`
- 답변 개수 배지: `badge bg-primary`
- 페이지 버튼(처음/이전/번호/다음/마지막)에 `th:data-page`를 넣고, 숨겨진 `<form id="searchForm"><input type="hidden" id="page" name="page">`를 둡니다.
- JS로 `document.getElementById('page').value = this.dataset.page;` 를 설정한 뒤 `submit()` 합니다.
- 시작/끝 페이지는 `th:with`로 계산합니다: `startPage=${(paging.number/10)*10}`

---

## 최종 구조

```
SBB1/src/main/java/com/mysite/sbb1
├── Sbb1Application.java
├── common/controller   StartController, indexController
├── question            controller / dto / entity / repository / service
└── answer              controller / dto / entity / repository / service
SBB1/src/main/resources
├── application.yml, logback-spring.xml
├── static/css, static/js         (bootstrap)
└── templates/ index, temp, layouts/layout, fragments/header·footer,
               question/list·detail·inputForm
```

---

## ⚠️ 주의할 점

1. ~~**`Answer.setContent()`가 아무 일도 하지 않습니다.**~~ → **해결됨.** 필드 이름 `countent`(오타)를 `content`로 바꾸고 빈 `setContent` 메서드를 지웠습니다. `AnswerService`의 `.content(...)`와 `detail.html`의 `answer.content`도 함께 바꿨습니다. 기존 답변 데이터를 그대로 쓰기 위해 DB 컬럼 이름은 `@Column(name = "countent")`로 유지합니다.
2. ~~**`application.yml` 오타**~~ → **해결됨.** `liveereload: enabled: ture`를 `livereload: enabled: true`로 고치고, `devtools` 아래에 잘못 들어가 있던 `thymeleaf: cache: false`를 `spring.thymeleaf.cache` 위치(`devtools`와 같은 깊이)로 옮겼습니다.
3. ~~`detail.html`과 `inputForm.html`의 `<style>`이 `layout:fragment="script"` 안에 있습니다.~~ → **해결됨.** `<style>`을 `layout:fragment="css"`로 옮겨서, 이제 `layout.html`의 `<head>` 안에 들어갑니다.
4. 테스트가 DB 상태에 의존합니다. `findById(1L)`, `size()==2` 같은 검사는 `testManyData`로 300개를 넣은 뒤에는 실패합니다. 처음부터 다시 만들 때는 테스트를 하나씩 순서대로 실행하세요.
