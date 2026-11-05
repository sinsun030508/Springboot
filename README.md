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

> 📘 **완전히 처음 만드는 사람용 상세 가이드는 [GUIDE.md](GUIDE.md)에 있습니다.**
> 프로그램 설치부터 MySQL 설정, 모든 파일의 전체 코드, 단계별 확인 방법, 테스트 실행 순서, 자주 나는 에러까지 담았습니다.
> 아래 내용은 수업에서 **주차별로 어떤 순서로 만들었는지** 요약한 것입니다.

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

## 폴더별 역할과 만들어진 순서

### 1) 폴더별 역할 (괄호 안은 처음 만들어진 주차)

```
SBB1/                                   프로젝트 루트 (1주차)
├── build.gradle, settings.gradle       사용할 라이브러리(의존성)와 빌드 설정
├── gradle/wrapper/, gradlew            Gradle을 따로 설치하지 않아도 빌드할 수 있게 해 주는 실행 파일
└── src/
    ├── main/java/com/mysite/sbb1/      실제 서비스 Java 코드 (1주차)
    │   ├── Sbb1Application.java        앱 시작점(main). 이 패키지 아래를 스캔해서 스프링 빈으로 등록
    │   ├── common/controller/          메인 화면처럼 특정 기능에 속하지 않는 공통 컨트롤러 (1주차)
    │   ├── question/                   "질문" 기능 묶음 (2주차)
    │   │   ├── entity/                 DB question 테이블과 연결되는 클래스 (2주차)
    │   │   ├── repository/             DB에 저장·조회하는 인터페이스 (3주차)
    │   │   ├── service/                조회·저장 등 실제 처리 로직 (5주차)
    │   │   ├── controller/             URL 요청을 받아 서비스 호출 후 화면 반환 (5주차)
    │   │   └── dto/                    화면 입력값을 담고 검증하는 객체 (8주차)
    │   └── answer/                     "답변" 기능 묶음 (3주차)
    │       ├── entity/                 DB answer 테이블과 연결되는 클래스 (3주차)
    │       ├── repository/             DB에 저장·조회하는 인터페이스 (5주차)
    │       ├── service/                답변 저장 로직 (7주차)
    │       ├── controller/             답변 등록 요청 처리 (7주차)
    │       └── dto/                    답변 입력값을 담고 검증하는 객체 (8주차)
    │
    ├── main/resources/                 Java가 아닌 설정 파일과 화면 파일 (1주차)
    │   ├── application.yml             포트, DB 연결, JPA, devtools 설정 (2주차, 1주차엔 application.properties)
    │   ├── logback-spring.xml          콘솔 로그 출력 형식 설정 (6주차)
    │   ├── static/                     변환 없이 그대로 내려보내는 파일 (1주차)
    │   │   ├── css/                    bootstrap.min.css (6주차)
    │   │   └── js/                     bootstrap.bundle.min.js (6주차)
    │   └── templates/                  Thymeleaf 화면(html). 컨트롤러가 반환한 이름으로 찾음 (1주차)
    │       ├── index.html, temp.html   메인 화면 / 레이아웃 연습 화면
    │       ├── question/               질문 목록·상세·등록 화면 (5주차)
    │       ├── layouts/                모든 화면이 공통으로 쓰는 뼈대 layout.html (6주차)
    │       └── fragments/              레이아웃에 끼워 넣는 조각 header.html, footer.html (6주차)
    │
    └── test/java/com/mysite/sbb1/      테스트 코드 (1주차)
        ├── Sbb1ApplicationTests.java   앱이 정상 실행되는지 확인
        ├── question/repository/        질문 저장·조회 쿼리 메서드 테스트 (3주차)
        └── answer/reoisitory/          답변 저장·조회 테스트 (5주차, 폴더 이름은 repository의 오타)
```

### 2) 만들어진 순서

| 주차 | 새로 만든 폴더 | 이유 |
|---|---|---|
| 1주차 | `common/controller`, `templates`, `static`, `test` | 프로젝트를 만들고 첫 화면(`/`)과 JSON 응답(`/start`)을 띄움 |
| 2주차 | `question/entity` | JPA로 DB 테이블을 만들기 시작 (`application.properties` → `application.yml`) |
| 3주차 | `answer/entity`, `question/repository`, `test/.../question/repository` | 질문-답변 관계를 만들고 DB 조회를 테스트로 연습 |
| 5주차 | `question/service`, `question/controller`, `answer/repository`, `templates/question`, `test/.../answer/reoisitory` | 컨트롤러 → 서비스 → 리포지토리 계층을 만들어 화면에 목록 출력 |
| 6주차 | `static/css`, `static/js`, `templates/layouts`, `templates/fragments` | Bootstrap을 넣고 모든 화면이 같은 레이아웃을 쓰도록 정리 |
| 7주차 | `answer/service`, `answer/controller` | 질문 등록, 답변 등록 기능 추가 |
| 8주차 | `question/dto`, `answer/dto` | 입력값 검증(Validation)을 위해 DTO 도입 |
| 9주차 | (새 폴더 없음) | 기존 `service`, `templates/question/list.html`에 페이징 추가 |

> 한 기능 안에서는 **entity → repository → service → controller → dto** 순서로 만들었습니다.
> DB 테이블(entity)을 먼저 만들고, 그 테이블을 다루는 쪽(repository → service)을 붙인 뒤,
> 마지막에 사용자 요청을 받는 쪽(controller)과 입력 검증(dto)을 만든 흐름입니다.

### 3) 요청이 처리되는 흐름 (폴더 간 관계)

```
브라우저 요청 (예: GET /question/list)
   ↓
controller   요청 URL을 받음, 화면에 넘길 데이터를 Model에 담음
   ↓  (입력값이 있으면 dto로 받아서 @Valid로 검증)
service      실제 처리 (조회, 저장, 페이징 계산 등)
   ↓
repository   DB에 SQL 실행 (JPA가 자동 생성)
   ↓
entity       DB 테이블의 한 줄(row)을 Java 객체로 표현
   ↓
templates    controller가 반환한 이름의 html에 데이터를 채워서 응답
             (layouts + fragments로 공통 틀, static의 css/js로 꾸밈)
```


---

## 네비게이션바(상단 메뉴) 설정 방법

화면 맨 위의 검은 메뉴바는 `templates/fragments/header.html`에 한 번만 만들어 두고,
`layouts/layout.html`이 모든 화면에 끼워 넣는 구조입니다. (6주차)

### 1) 준비: Bootstrap 파일 넣기

네비게이션바 모양은 Bootstrap CSS가, 접기 버튼과 드롭다운 동작은 Bootstrap JS가 담당합니다. 두 파일이 모두 있어야 합니다.

- `static/css/bootstrap.min.css`
- `static/js/bootstrap.bundle.min.js` ← 드롭다운 위치 계산에 필요한 Popper가 포함된 bundle 버전을 써야 함

`layout.html`에서 CSS는 `<head>`에, JS는 `<body>` 맨 끝에 불러옵니다.

```html
<head>
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
</head>
<body>
    ...
    <script th:src="@{/js/bootstrap.bundle.min.js}"></script>
</body>
```

### 2) `fragments/header.html` 만들기

`th:fragment="header"`로 이름을 붙여 두면 다른 파일에서 이 조각을 가져다 쓸 수 있습니다.
아래 코드는 실제 `header.html`에서 설명에 필요 없는 접근성 속성(`aria-*`)과 일부 메뉴를 빼고 줄인 것입니다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div th:fragment="header">
    <!-- navbar-expand-lg : 화면이 lg(992px) 이상이면 메뉴를 펼치고, 작으면 ☰ 버튼으로 접음
         navbar-dark bg-dark : 어두운 배경 + 밝은 글자 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">

            <!-- ① 왼쪽 로고(사이트 이름). 누르면 메인 화면으로 -->
            <a class="navbar-brand" href="/">신선호 SBB</a>

            <!-- ② 모바일용 ☰ 버튼. data-bs-target의 id와 아래 collapse div의 id가 같아야 동작 -->
            <button class="navbar-toggler" type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!-- ③ 접히는 영역 : 메뉴 목록 + 검색창 -->
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <!-- me-auto : 메뉴를 왼쪽에 붙이고 남은 공간을 오른쪽(검색창 쪽)으로 밀어냄 -->
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link active" href="/">Home</a>       <!-- active : 강조 표시 -->
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/question/list">질문 목록</a>
                    </li>

                    <!-- 드롭다운 메뉴 : data-bs-toggle="dropdown"이 있어야 눌렀을 때 펼쳐짐 -->
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button"
                           data-bs-toggle="dropdown">Dropdown</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="#">Action</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#">Something else here</a></li>
                        </ul>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link disabled" aria-disabled="true">Disabled</a>  <!-- 못 누르는 메뉴 -->
                    </li>
                </ul>

                <!-- 검색창 (지금은 모양만 있고 검색 기능은 연결되어 있지 않음) -->
                <form class="d-flex" role="search">
                    <input class="form-control me-2" type="search" placeholder="Search">
                    <button class="btn btn-outline-light" type="submit">Search</button>
                </form>
            </div>
        </div>
    </nav>
</div>
</html>
```

### 3) 레이아웃에 끼워 넣기

`layouts/layout.html`의 `<body>` 맨 위에서 header 조각을 불러옵니다.
`~{파일경로 :: 조각이름}` 형식이고, 파일 경로는 `templates/` 기준이며 `.html`은 생략합니다.

```html
<body class="d-flex flex-column min-vh-100">
    <header th:replace="~{fragments/header :: header}"></header>   <!-- 이 태그가 header 조각으로 통째로 바뀜 -->
    <main class="flex-grow-1" layout:fragment="content"></main>
    <footer th:replace="~{fragments/footer :: footer}"></footer>
    <script th:src="@{/js/bootstrap.bundle.min.js}"></script>
</body>
```

각 화면(`index.html`, `question/list.html` 등)은 `layout:decorate="~{layouts/layout}"`만 적으면
네비게이션바가 자동으로 들어갑니다. 화면마다 메뉴 코드를 복사할 필요가 없습니다.

### 4) 자주 바꾸는 설정

| 하고 싶은 것 | 바꾸는 곳 |
|---|---|
| 사이트 이름 변경 | `<a class="navbar-brand">` 안의 글자 (탭 제목은 `layout.html`의 `<title>`) |
| 메뉴 추가 | `<ul class="navbar-nav">` 안에 `<li class="nav-item"><a class="nav-link" href="/주소">이름</a></li>` 한 줄 추가 |
| 메뉴 삭제 | 해당 `<li>` 통째로 지우기 (예: 연습용 Dropdown, Disabled 메뉴) |
| 색상 변경 | `bg-dark` → `bg-primary`(파랑), `bg-success`(초록) 등. 밝은 배경이면 `navbar-dark` → `navbar-light`로 같이 바꾸고 버튼도 `btn-outline-dark`로 |
| 접히는 기준 변경 | `navbar-expand-lg` → `navbar-expand-md`(768px) / `navbar-expand`(항상 펼침) |
| 메뉴를 오른쪽 정렬 | `<ul>`의 `me-auto` → `ms-auto` |
| 검색창 없애기 | `<form class="d-flex">` 통째로 지우기 |

> **링크 주소 팁** : 지금은 `href="/question/list"`처럼 주소를 직접 적었습니다.
> Thymeleaf의 `th:href="@{/question/list}"`로 쓰면 나중에 서버 주소 앞부분(context path)이 바뀌어도 링크가 자동으로 맞춰집니다.
> 새 메뉴를 추가할 때는 이 방식을 권장합니다.

### 5) 동작하지 않을 때 확인할 것

- **☰ 버튼이나 드롭다운이 눌러도 안 열림**
  - `bootstrap.bundle.min.js`가 불러와지는지 확인 (브라우저 F12 → Console에 404 에러가 없는지)
  - `bootstrap.min.js`(bundle이 아닌 것)를 쓰면 드롭다운이 동작하지 않음
- **☰ 버튼은 보이는데 메뉴가 안 펼쳐짐** → 버튼의 `data-bs-target="#아이디"`와 `<div class="collapse" id="아이디">`가 같은지 확인
- **네비게이션바가 아예 안 보임** → `layout.html`의 `th:replace` 경로(`fragments/header`)와 조각 이름(`header`), 그리고 그 화면에 `layout:decorate`가 있는지 확인
- **모양이 깨짐(일반 글자 목록처럼 보임)** → `bootstrap.min.css` 경로 확인

---

## ⚠️ 주의할 점

1. ~~**`Answer.setContent()`가 아무 일도 하지 않습니다.**~~ → **해결됨.** 필드 이름 `countent`(오타)를 `content`로 바꾸고 빈 `setContent` 메서드를 지웠습니다. `AnswerService`의 `.content(...)`와 `detail.html`의 `answer.content`도 함께 바꿨습니다. 기존 답변 데이터를 그대로 쓰기 위해 DB 컬럼 이름은 `@Column(name = "countent")`로 유지합니다.
2. ~~**`application.yml` 오타**~~ → **해결됨.** `liveereload: enabled: ture`를 `livereload: enabled: true`로 고치고, `devtools` 아래에 잘못 들어가 있던 `thymeleaf: cache: false`를 `spring.thymeleaf.cache` 위치(`devtools`와 같은 깊이)로 옮겼습니다.
3. ~~`detail.html`과 `inputForm.html`의 `<style>`이 `layout:fragment="script"` 안에 있습니다.~~ → **해결됨.** `<style>`을 `layout:fragment="css"`로 옮겨서, 이제 `layout.html`의 `<head>` 안에 들어갑니다.
4. 테스트가 DB 상태에 의존합니다. `findById(1L)`, `size()==2` 같은 검사는 `testManyData`로 300개를 넣은 뒤에는 실패합니다. 처음부터 다시 만들 때는 테스트를 하나씩 순서대로 실행하세요.
