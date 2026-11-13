# SBB1 완전 처음부터 만들기 (상세 가이드)

스프링부트를 처음 해 보는 사람도 **이 문서만 보고 위에서부터 따라 하면** 지금 저장소와 똑같은 게시판(질문/답변) 프로젝트를 만들 수 있도록 정리했습니다.

- 모든 파일의 **전체 코드**가 들어 있습니다. 아래 코드는 저장소의 실제 파일을 그대로 옮긴 것입니다.
- 단계마다 **✅ 확인** 항목이 있습니다. 확인이 안 되면 다음 단계로 넘어가지 말고 [자주 나는 에러](#16-자주-나는-에러와-해결-방법)를 먼저 보세요.
- 수업에서 주차별로 만든 순서와 이유는 [README.md](README.md)의 주차별 가이드에 있습니다. 이 문서는 **완성본을 한 번에 다시 만드는 순서**입니다.

## 목차

0. [설치할 프로그램](#0-설치할-프로그램)
1. [MySQL에 데이터베이스 만들기](#1-mysql에-데이터베이스-만들기)
2. [프로젝트 만들기 (start.spring.io)](#2-프로젝트-만들기-startspringio)
3. [IntelliJ로 열고 설정하기](#3-intellij로-열고-설정하기)
4. [build.gradle 작성](#4-buildgradle-작성)
5. [설정 파일 작성 (application.yml, logback-spring.xml)](#5-설정-파일-작성)
6. [패키지(폴더) 만들기](#6-패키지폴더-만들기)
7. [엔티티 만들기 → DB 테이블 자동 생성](#7-엔티티-만들기)
8. [리포지토리 만들기](#8-리포지토리-만들기)
9. [DTO 만들기](#9-dto-만들기)
10. [서비스 만들기](#10-서비스-만들기)
11. [Bootstrap 파일 넣기](#11-bootstrap-파일-넣기)
12. [공통 레이아웃 (layout, header, footer)](#12-공통-레이아웃)
13. [메인 화면 (index, temp, 공통 컨트롤러)](#13-메인-화면)
14. [질문/답변 화면과 컨트롤러](#14-질문답변-화면과-컨트롤러)
15. [테스트 코드 작성과 실행 순서](#15-테스트-코드)
16. [자주 나는 에러와 해결 방법](#16-자주-나는-에러와-해결-방법)
17. [GitHub에 올리기 (선택)](#17-github에-올리기-선택)
18. [최종 파일 체크리스트](#18-최종-파일-체크리스트)

---

## 0. 설치할 프로그램

| 프로그램 | 버전 | 용도 | 받는 곳 |
|---|---|---|---|
| JDK | **21** | Java 실행/컴파일 | https://adoptium.net (Temurin 21) 또는 IntelliJ 안에서 다운로드 |
| IntelliJ IDEA | 최신 버전 | 코드 작성, 실행 | https://www.jetbrains.com/idea/download |
| MySQL Community Server | 8.x | 데이터베이스 | https://dev.mysql.com/downloads/mysql/ |
| MySQL Workbench | 8.x | DB 내용을 눈으로 확인 (선택) | https://dev.mysql.com/downloads/workbench/ |
| Git | 최신 | GitHub에 올릴 때 (선택) | https://git-scm.com |

**MySQL 설치할 때 주의**
- 설치 중에 **root 비밀번호**를 정하는 화면이 나옵니다. 이 프로젝트 설정 파일은 비밀번호를 `0000`으로 가정합니다.
  - 다른 비밀번호를 썼다면 5단계 `application.yml`의 `password: '0000'` 부분을 내 비밀번호로 바꾸면 됩니다.
- 포트는 기본값 **3306** 그대로 둡니다.

✅ **확인**: 명령 프롬프트(cmd)나 터미널에서 `java -version`을 입력했을 때 `21`이 보이면 JDK가 설치된 것입니다. (IntelliJ에서 JDK를 받았다면 이 명령은 안 될 수도 있는데, IntelliJ 안에서만 쓸 거면 괜찮습니다.)

---

## 1. MySQL에 데이터베이스 만들기

MySQL Workbench(또는 MySQL Command Line Client)에 root로 접속한 뒤 아래 SQL을 실행합니다.

```sql
CREATE DATABASE sbb_c DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

- `sbb_c`가 이 프로젝트가 사용할 DB 이름입니다. (`application.yml`의 `jdbc:mysql://localhost:3306/sbb_c`와 같아야 합니다.)
- **테이블은 직접 만들지 않습니다.** 7단계에서 엔티티 클래스를 만들고 실행하면 JPA가 자동으로 만들어 줍니다.

✅ **확인**: `SHOW DATABASES;`를 실행하면 목록에 `sbb_c`가 보입니다.

---

## 2. 프로젝트 만들기 (start.spring.io)

1. 브라우저에서 https://start.spring.io 접속
2. 아래처럼 선택합니다.

| 항목 | 선택 값 |
|---|---|
| Project | **Gradle - Groovy** |
| Language | **Java** |
| Spring Boot | **4.1.x** (목록에 없으면 가장 가까운 4.x 아무거나. 4단계에서 build.gradle을 통째로 바꾸기 때문에 괜찮음) |
| Group | `com.mysite` |
| Artifact | `SBB1` |
| Name | `SBB1` |
| Package name | `com.mysite.sbb1` (자동으로 채워짐) |
| Packaging | Jar |
| Java | **21** |

3. 오른쪽 **ADD DEPENDENCIES** 버튼으로 아래를 추가합니다. (4단계에서 build.gradle을 덮어쓰므로 빠뜨려도 괜찮지만, 넣어 두면 편합니다.)
   - Spring Web
   - Thymeleaf
   - Spring Data JPA
   - MySQL Driver
   - Lombok
   - Validation
   - Spring Boot DevTools
   - GraalVM Native Support
4. **GENERATE** 버튼 → `SBB1.zip`이 다운로드됩니다.
5. 압축을 풉니다. 이 폴더(`SBB1`)가 프로젝트 폴더입니다.
   - 경로에 한글이나 공백이 없는 곳에 두는 것이 안전합니다. 예: `C:\work\Springboot\SBB1`

---

## 3. IntelliJ로 열고 설정하기

1. IntelliJ 실행 → **Open** → 압축 푼 `SBB1` 폴더 선택 → **Trust Project**
2. 오른쪽 아래에 Gradle이 라이브러리를 내려받는 진행 표시가 나옵니다. 처음에는 몇 분 걸릴 수 있으니 끝날 때까지 기다립니다.
3. **JDK 21 지정**
   - `File` → `Project Structure` → `Project` → **SDK: 21** 선택 (없으면 `Add SDK` → `Download JDK` → 버전 21)
   - `File` → `Settings`(맥은 `IntelliJ IDEA` → `Settings`) → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → **Gradle JVM: 21**
4. **Lombok 설정**
   - `Settings` → `Plugins`에서 **Lombok**이 설치(활성화)되어 있는지 확인 (보통 기본 포함)
   - `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → **Enable annotation processing** 체크
   - 이걸 안 하면 코드는 맞는데 `getSubject()`, `builder()` 같은 메서드를 못 찾는다는 빨간 줄이 생깁니다.

✅ **확인**: 왼쪽 프로젝트 창에 아래 구조가 보이면 됩니다.

```
SBB1
├── build.gradle
├── settings.gradle
├── gradlew, gradlew.bat
└── src
    ├── main
    │   ├── java/com/mysite/sbb1/Sbb1Application.java
    │   └── resources/
    │       ├── application.properties
    │       ├── static/
    │       └── templates/
    └── test/java/com/mysite/sbb1/Sbb1ApplicationTests.java
```

---

## 4. build.gradle 작성

`SBB1/build.gradle` 파일 내용을 **전부 지우고** 아래 내용으로 바꿉니다.

📄 **`SBB1/build.gradle`**

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.1'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.graalvm.buildtools.native' version '1.1.8'
}

group = 'com.mysite'
version = '0.0.1-SNAPSHOT'
description = 'SBB1'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:4.0.1'
    implementation 'org.springframework.boot:spring-boot-starter-validation'



    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-thymeleaf-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'

    implementation 'com.mysql:mysql-connector-j:8.4.0'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    testCompileOnly 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

| 의존성 | 하는 일 |
|---|---|
| `spring-boot-starter-webmvc` | 웹 서버(내장 톰캣)와 컨트롤러 기능 |
| `spring-boot-starter-thymeleaf` | html 화면 템플릿 엔진 |
| `thymeleaf-layout-dialect` | 공통 레이아웃(`layout:decorate`, `layout:fragment`) 기능 |
| `spring-boot-starter-data-jpa` | 엔티티/리포지토리로 DB 다루기 (SQL 자동 생성) |
| `mysql-connector-j` | MySQL 접속 드라이버 |
| `spring-boot-starter-validation` | `@NotEmpty`, `@Size` 같은 입력값 검증 |
| `lombok` | `@Getter`, `@Builder` 등으로 반복 코드 자동 생성 |
| `spring-boot-devtools` | 코드를 고치면 서버 자동 재시작 (개발할 때만 사용) |

파일을 저장하면 오른쪽 위에 **코끼리 모양(🐘) Load Gradle Changes** 버튼이 뜹니다. 눌러서 라이브러리를 다시 받습니다.
(안 보이면 오른쪽 `Gradle` 탭 → 새로고침 버튼)

`settings.gradle`은 아래 한 줄이면 됩니다. (자동으로 만들어져 있음)

```groovy
rootProject.name = 'SBB1'
```

✅ **확인**: Gradle 로딩이 에러 없이 끝나고, `build.gradle`에 빨간 줄이 없습니다.

---

## 5. 설정 파일 작성

### 5-1. application.yml

1. `src/main/resources/application.properties` 파일을 **삭제**합니다. (같은 설정을 yml 형식으로 쓰기 위해)
2. `src/main/resources`에서 오른쪽 클릭 → `New` → `File` → 이름 `application.yml`
3. 아래 내용을 붙여 넣습니다.

> ⚠️ yml은 **들여쓰기(띄어쓰기 2칸)가 곧 문법**입니다. Tab 문자를 쓰면 안 되고, 칸 수가 하나만 틀려도 설정이 적용되지 않거나 실행이 안 됩니다.

📄 **`SBB1/src/main/resources/application.yml`**

```yaml
####################################
# Port & Project Name
####################################
server:
  port: 8080                    # 포트 번호

spring:
  application:
    name: SBB

  devtools:
    restart:
      enabled: true

    livereload:
      enabled: true

  thymeleaf:
    cache: false


  ####################################
  # MySQL 설정
  ####################################
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sbb_c?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: '0000'

  ####################################
  # JPA 설정
  ####################################
  jpa:
    open-in-view: true      # JPA 영속성 컨텍스트를 뷰까지 유지할지 여부
    hibernate:
      ddl-auto: update      # create: 실행 시킬때 마다 지움, update: 값을 유지, ...
      naming:
        physical-strategy: org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy # 카멜케이스를 언더스코어로 변환
    show-sql: true          # SQL 출력 여부
    properties:
      hibernate:
        format_sql: true    # SQL 포맷팅 여부

  servlet:
    encoding:
      charset: UTF-8        # 인코딩 설정
```

- DB 이름(`sbb_c`), 계정(`root`), 비밀번호(`'0000'`)는 1단계에서 만든 것과 같아야 합니다.
- `ddl-auto: update` 덕분에 엔티티 클래스를 만들면 테이블이 자동으로 생기고, 다시 실행해도 데이터가 지워지지 않습니다.

### 5-2. logback-spring.xml (콘솔 로그 형식)

`src/main/resources`에 `logback-spring.xml` 파일을 만들고 붙여 넣습니다. (없어도 실행은 되지만, 로그가 짧고 보기 좋게 바뀝니다.)

📄 **`SBB1/src/main/resources/logback-spring.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="30 seconds">
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />

    <property name="CONSOLE_LOG_PATTERN" value="${CONSOLE_LOG_PATTERN:-%clr([${LOG_LEVEL_PATTERN:-%5p}]) : %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m %n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}" />
    <property name="CONSOLE_LOG_CHARSET" value="${CONSOLE_LOG_CHARSET:-default}" />
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>${CONSOLE_LOG_CHARSET}</charset>
        </encoder>
    </appender>

    <include resource="org/springframework/boot/logging/logback/file-appender.xml" />

    <!-- SQL 보여주기 -->
    <logger name="jdbc.sqlonly" level="INFO" appender-ref="console-infolog" />

    <!-- 조회 결과를 테이블로 보여주기 -->
    <logger name="jdbc.resultsettable" level="INFO" appender-ref="console-infolog" />
    <logger name="org.springframework" level="error" />
    <logger name="org.springframework.jdbc" level="error" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 5-3. 첫 실행

`src/main/java/com/mysite/sbb1/Sbb1Application.java`는 자동으로 만들어져 있습니다. 아래와 같은지 확인합니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/Sbb1Application.java`**

```java
package com.mysite.sbb1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 스프링부트 애플리케이션의 시작점(main 클래스)
 * - @SpringBootApplication : 자동 설정 + 컴포넌트 스캔 + 설정 클래스 역할을 한 번에 지정
 * - 이 클래스가 있는 패키지(com.mysite.sbb1)와 그 하위 패키지의
 *   @Controller, @Service, @Repository 등을 자동으로 찾아 스프링 빈으로 등록함
 */
@SpringBootApplication
public class Sbb1Application {

    // 내장 톰캣 서버를 띄우고 스프링 애플리케이션을 실행 (기본 포트: application.yml의 8080)
    public static void main(String[] args) {
        SpringApplication.run(Sbb1Application.class, args);
    }

}
```

실행 방법 (둘 중 하나)
- IntelliJ: `Sbb1Application.java`를 열고 `main` 옆 **초록색 ▶** → `Run 'Sbb1Application'`
- 터미널: `SBB1` 폴더에서 `gradlew.bat bootRun` (윈도우) / `./gradlew bootRun` (맥, 리눅스)

✅ **확인**
- 콘솔 마지막 쪽에 `Started Sbb1Application in ... seconds`가 나오면 성공입니다.
- 브라우저에서 http://localhost:8080 을 열면 **Whitelabel Error Page (404)** 가 나옵니다. 아직 화면을 안 만들었으니 **정상**입니다.
- 실행이 실패하면 대부분 MySQL 접속 문제입니다 → [16. 자주 나는 에러](#16-자주-나는-에러와-해결-방법)

확인했으면 빨간 네모(■) 버튼으로 서버를 멈춥니다. 이후 단계에서도 코드를 다 쓴 뒤 다시 실행하면 됩니다.

---

## 6. 패키지(폴더) 만들기

`src/main/java/com/mysite/sbb1`에서 오른쪽 클릭 → `New` → `Package`로 아래 패키지를 만듭니다.
이름에 점(`.`)을 넣으면 하위 폴더까지 한 번에 만들어집니다. 예: `question.entity` 입력 → `question/entity` 폴더 생성

```
com.mysite.sbb1
├── common.controller        공통 컨트롤러 (메인 화면)
├── question.entity          질문 테이블
├── question.repository      질문 DB 접근
├── question.dto             질문 입력값
├── question.service         질문 처리 로직
├── question.controller      질문 URL 처리
├── answer.entity            답변 테이블
├── answer.repository        답변 DB 접근
├── answer.dto               답변 입력값
├── answer.service           답변 처리 로직
└── answer.controller        답변 URL 처리
```

`src/main/resources/templates` 아래에는 `New` → `Directory`로 폴더 3개를 만듭니다.

```
templates
├── layouts       공통 뼈대
├── fragments     header, footer 조각
└── question      질문 화면들
```

`src/main/resources/static` 아래에는 `css`, `js` 폴더를 만듭니다.

> 각 폴더의 역할과 실제로 몇 주차에 만들어졌는지는 README의 "폴더별 역할과 만들어진 순서"에 정리되어 있습니다.

---

## 7. 엔티티 만들기

엔티티는 **DB 테이블과 1:1로 연결되는 클래스**입니다. 필드 하나가 컬럼 하나가 됩니다.
패키지에서 오른쪽 클릭 → `New` → `Java Class`로 만듭니다.

질문(Question)과 답변(Answer)은 서로를 참조하기 때문에 **두 파일을 모두 만들어야** 빨간 줄이 없어집니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/question/entity/Question.java`**

```java
package com.mysite.sbb1.question.entity;

import com.mysite.sbb1.answer.entity.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 엔티티 → DB의 question 테이블과 연결되는 클래스
 * - 필드 하나가 테이블의 컬럼 하나가 됨
 * - application.yml의 ddl-auto: update 설정 때문에 실행하면 테이블이 자동으로 만들어짐
 */
@Entity //테이블을 만들음
@Getter
@Setter
@ToString(exclude = "answerList") // answerList를 빼지 않으면 Question ↔ Answer가 서로 toString을 호출해서 무한 반복됨
@NoArgsConstructor  //기본 생성자
@AllArgsConstructor //전체 생성자
@Builder            // Question.builder().subject(..).build() 형태로 객체를 만들 수 있게 해줌
public class Question {

    // 기본키(PK). IDENTITY : DB의 auto_increment로 1, 2, 3... 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 200, nullable = false) // 최대 200자, 빈 값(null) 저장 불가
    private String subject; //질문 제목

    @Column(columnDefinition = "TEXT")      // 글자 수 제한 없는 TEXT 타입
    private String content; //질문 내용

    private LocalDateTime created; //생성일

    /**
     * 이 질문에 달린 답변 목록 (1:N 관계, 질문 1개 - 답변 여러 개)
     * - mappedBy = "question" : 외래키는 Answer 쪽의 question 필드가 관리함
     * - cascade = ALL : 질문을 저장/삭제하면 답변도 같이 저장/삭제됨
     * - orphanRemoval = true : 목록에서 빠진 답변은 DB에서도 삭제됨
     * - LAZY : 실제로 answerList를 사용할 때 DB에서 조회함
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;



}
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/answer/entity/Answer.java`**

```java
package com.mysite.sbb1.answer.entity;

import com.mysite.sbb1.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 답변 엔티티 → DB의 answer 테이블과 연결되는 클래스
 * - 답변 하나는 반드시 질문 하나에 속함 (N:1 관계)
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor //전체 생성자
@Builder
public class Answer {

    // 기본키(PK), auto_increment로 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 답변 내용. 예전 필드 이름(countent, 오타)으로 만들어진 DB 컬럼을 그대로 사용하기 위해 name을 지정
    @Column(name = "countent", columnDefinition = "TEXT") // DB 컬럼 이름은 기존 그대로 유지
    private String content;

    private LocalDateTime created; // 답변 작성일

    /**
     * 이 답변이 달린 질문 (N:1 관계, 답변 여러 개 - 질문 1개)
     * - @ManyToOne의 기본값은 EAGER(항상 같이 조회)인데, 필요할 때만 조회하도록 LAZY로 지정
     * - @JoinColumn : answer 테이블에 question_id 외래키(FK) 컬럼을 만들고, 빈 값은 허용하지 않음
     */
    //FetchType fetch() default FetchType.EAGER; 조인
    @ManyToOne(fetch = FetchType.LAZY)  // M:1관계
    @JoinColumn(name = "question_id", nullable = false) //포린키
    private Question question;
}
```

- `Answer`의 `content` 필드는 DB에서 `countent`라는 컬럼 이름을 씁니다. 예전에 오타로 만든 컬럼에 있는 데이터를 그대로 쓰려고 `@Column(name = "countent")`로 지정해 둔 것입니다. 새로 만드는 거라면 그대로 두어도 되고, `name = "countent"` 부분을 지워서 컬럼 이름도 `content`로 만들어도 됩니다.

✅ **확인**: 서버를 실행한 뒤 Workbench에서 아래를 실행합니다.

```sql
USE sbb_c;
SHOW TABLES;      -- answer, question 두 개가 보이면 성공
DESC question;    -- id, subject, content, created 컬럼이 있으면 성공 (순서는 달라도 됨)
DESC answer;      -- id, countent, created, question_id 컬럼이 있으면 성공
```

---

## 8. 리포지토리 만들기

리포지토리는 **DB에 저장/조회하는 기능**입니다. 인터페이스만 만들면 스프링이 구현을 자동으로 만들어 줍니다.
`New` → `Java Class` 창에서 종류를 **Interface**로 선택해서 만듭니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/question/repository/QuestionRepository.java`**

```java
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
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/answer/repository/AnswerRespository.java`**

```java
package com.mysite.sbb1.answer.repository;

import com.mysite.sbb1.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * answer 테이블에 접근하는 리포지토리
 * - JpaRepository를 상속받아 save(), findById(), findAll() 등 기본 기능을 그대로 사용
 */
public interface AnswerRespository extends JpaRepository<Answer, Long>{
}
```

- 파일 이름 `AnswerRespository`는 Repository의 오타지만, 다른 파일들이 이 이름으로 사용하고 있으니 **똑같이** 만들어야 합니다.

---

## 9. DTO 만들기

DTO는 **화면(폼)에서 입력한 값을 담아 오는 객체**입니다. 여기에 붙인 `@NotEmpty`, `@Size`로 입력값을 검증합니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/question/dto/QuestionDto.java`**

```java
package com.mysite.sbb1.question.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 등록 폼(inputForm.html)에서 입력한 값을 담아서 전달하는 객체 (DTO: Data Transfer Object)
 * - 엔티티(Question)를 화면에 직접 노출하지 않고, 입력에 필요한 값만 받기 위해 사용
 * - 컨트롤러에서 @Valid를 붙이면 아래 검증 어노테이션이 동작하고,
 *   실패하면 message 내용이 화면에 에러로 표시됨
 */
@Data   //getter,setter등등 기능 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuestionDto {
    @NotEmpty(message = "제목은 필수 항목입니다.")   //null, 빈 문자열("") 허용 안함
    @Size(max = 150, message = "제목은 150자 이하여야 합니다.")  // 최대 길이 검사
    private String subject;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/answer/dto/AnswerDto.java`**

```java
package com.mysite.sbb1.answer.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 상세 화면(detail.html)의 답변 등록 폼에서 입력한 값을 담는 DTO
 * - 컨트롤러에서 @Valid를 붙이면 내용이 비어 있는지 검증함
 */
@Data   //getter,setter등등 기능 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDto {

    @NotEmpty(message = "내용은 필수 항목입니다.")  // 빈 답변은 등록할 수 없음
    private String content;

}
```

---

## 10. 서비스 만들기

서비스는 **실제 처리(조회, 저장, 페이징 계산)를 담당**합니다. 컨트롤러는 서비스를 호출하기만 합니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/question/service/QuestionService.java`**

```java
package com.mysite.sbb1.question.service;

import com.mysite.sbb1.question.dto.QuestionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 질문 관련 비즈니스 로직을 처리하는 서비스
 * - 컨트롤러 → 서비스 → 리포지토리 → DB 순서로 호출됨
 * - 컨트롤러는 요청/응답만 담당하고, 실제 처리(조회, 저장 등)는 서비스가 담당
 * - @RequiredArgsConstructor : final 필드를 받는 생성자를 자동으로 만들어서
 *   스프링이 QuestionRepository를 주입(DI)해 줌
 */
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 질문 목록을 페이지 단위로 조회 (질문 목록 화면에서 사용)
     * @param page 조회할 페이지 번호 (0부터 시작)
     * @return 한 페이지에 10개씩, 작성일(created) 최신순으로 정렬된 질문 목록
     */
    public Page<Question> getList(int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("created").descending());
        //Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "created");
        Page<Question> paging = questionRepository.findAll(pageable);
        return paging;  // alt + shift + i (inline variable)로 간략화 가능
    }

    /**
     * 질문 등록 : 폼에서 받은 QuestionDto로 Question 엔티티를 만들어 DB에 저장
     * 작성일(created)은 저장하는 현재 시간으로 넣음
     */
    public void createeQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .subject(questionDto.getSubject())
                .content(questionDto.getContent())
                .created(LocalDateTime.now())
                .build();
        questionRepository.save(question);
    }

    /**
     * id로 질문 1개 조회 (질문 상세 화면, 답변 등록에서 사용)
     * 해당 id의 질문이 없으면 EntityNotFoundException 예외 발생
     */
    public Question getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터 없음"));

        return question;




        // 간략한 표현
//    Question question = questionRepository.findById(id)
//        .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
//    return question;
//    Optional<Question> question = questionRepository.findById(id);
//        if(question.isPresent()) {
//            return question.get();
//        } else {
//            throw new IllegalArgumentException("id에 해당하는 질문이 없습니다 : " + id);
//        }
    }

}
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/answer/service/AnswerService.java`**

```java
package com.mysite.sbb1.answer.service;


import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.answer.entity.Answer;
import com.mysite.sbb1.answer.repository.AnswerRespository;
import com.mysite.sbb1.question.entity.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 답변 관련 비즈니스 로직을 처리하는 서비스 (DB 저장은 AnswerRespository를 통해 처리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {

    private final AnswerRespository answerRespository;

    /**
     * 답변 등록
     * - 어떤 질문에 달 답변인지(question)와 입력한 내용(answerDto)으로 Answer 엔티티를 만들어 저장
     * - 작성일(created)은 저장하는 현재 시간
     */
    public void createAnswer(Question question, AnswerDto answerDto) {
        Answer answer = Answer.builder()
                .content(answerDto.getContent())
                .question(question)
                .created(LocalDateTime.now())
                .build();

        Answer saved =  answerRespository.save(answer);
        log.info("======= saved : {}", saved);
    }

}
```

- 질문 등록 메서드 이름이 `createeQuestion`(e가 두 개)입니다. 컨트롤러와 테스트도 이 이름을 쓰니 **똑같이** 적어야 합니다.

✅ **확인**: 메뉴 `Build` → `Build Project`를 눌렀을 때 에러가 없으면 7~10단계가 제대로 된 것입니다.

---

## 11. Bootstrap 파일 넣기

화면 디자인에 쓰는 Bootstrap(v5.3.8) 파일 2개를 `static` 폴더에 넣습니다.

**방법 A (공식 사이트)**
1. https://getbootstrap.com/docs/5.3/getting-started/download/ → **Compiled CSS and JS** 의 `Download` 클릭
2. 압축을 풀고 아래 두 파일만 복사합니다.
   - `css/bootstrap.min.css` → `src/main/resources/static/css/bootstrap.min.css`
   - `js/bootstrap.bundle.min.js` → `src/main/resources/static/js/bootstrap.bundle.min.js`

**방법 B (주소에서 바로 저장)**: 아래 주소를 브라우저로 열고 `Ctrl + S`로 같은 위치에 저장합니다.
- https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css
- https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js

> JS는 꼭 **bundle** 버전이어야 합니다. 일반 `bootstrap.min.js`에는 드롭다운에 필요한 Popper가 없어서 메뉴가 안 열립니다.

---

## 12. 공통 레이아웃

모든 화면이 같이 쓰는 뼈대(layout)와, 거기에 끼워 넣는 위쪽 메뉴(header), 아래쪽(footer)을 만듭니다.
html 파일은 폴더에서 오른쪽 클릭 → `New` → `HTML File`(또는 `File`)로 만듭니다.

📄 **`SBB1/src/main/resources/templates/layouts/layout.html`**

```html
<!DOCTYPE html>
<html lang="ko"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>신선호 SBB</title>

    <!-- 공통 Bootstrap CSS (로컬 정적 리소스: src/main/resources/static/css/...) -->
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
    <script src="http://localhost:35729/livereload.js?snipver=1"></script>

    <!-- 개별 페이지 전용 CSS 영역 -->
    <th:block layout:fragment="css"></th:block>
</head>
<body class="d-flex flex-column min-vh-100">

<!-- Header 영역 (필요 시 주석 해제하여 fragment 경로 지정) -->
 <header th:replace="~{fragments/header :: header}"></header>

<!-- 개별 페이지 본문 영역 -->
<main class="flex-grow-1" layout:fragment="content">
    <!-- 각 화면 뷰 템플릿의 본문이 이 자리에 삽입됩니다. -->
</main>

<!-- Footer 영역 (필요 시 주석 해제하여 fragment 경로 지정) -->
<!--<footer th:replace="~{fragments/footer :: footer}"></footer>-->
<footer th:replace="~{fragments/footer :: footer}"></footer>

<!-- 공통 Bootstrap JS 번들 (로컬 정적 리소스: src/main/resources/static/js/...) -->
<script th:src="@{/js/bootstrap.bundle.min.js}"></script>

<!-- 개별 페이지 전용 Script 영역 -->
<th:block layout:fragment="script"></th:block>
</body>
</html>
```

- `layout:fragment="content"` 자리에 각 화면의 본문이 들어갑니다.
- `layout:fragment="css"`, `layout:fragment="script"` 자리에는 화면마다 필요한 CSS/JS가 들어갑니다.
- `livereload.js` 줄은 코드 수정 시 브라우저 자동 새로고침용입니다. 브라우저 콘솔에 이 파일 관련 에러가 보여도 화면에는 영향이 없습니다.

📄 **`SBB1/src/main/resources/templates/fragments/header.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<!-- 모든 페이지 위쪽에 들어가는 공통 메뉴(네비게이션 바)
     layout.html에서 th:replace="~{fragments/header :: header}"로 불러옴 -->
<div th:fragment="header">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="/">신선호 SBB</a>

            <!-- 화면이 좁을 때(모바일) 메뉴를 접었다 펴는 버튼 -->
            <button class="navbar-toggler" type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="/">Home</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="/question/list">질문 목록</a>
                    </li>

                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#"
                           role="button" data-bs-toggle="dropdown"
                           aria-expanded="false">
                            Dropdown
                        </a>

                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="#">Action</a></li>
                            <li><a class="dropdown-item" href="#">Another action</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#">Something else here</a></li>
                        </ul>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link disabled" aria-disabled="true">Disabled</a>
                    </li>
                </ul>

                <!-- 검색창 (아직 기능은 연결되지 않은 화면용) -->
                <form class="d-flex" role="search">
                    <input class="form-control me-2" type="search"
                           placeholder="Search" aria-label="Search"/>
                    <button class="btn btn-outline-light" type="submit">
                        Search
                    </button>
                </form>
            </div>
        </div>
    </nav>
</div>
```
📄 **`SBB1/src/main/resources/templates/fragments/footer.html`**

```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<!-- 모든 페이지 아래쪽에 들어가는 공통 footer
     layout.html에서 th:replace="~{fragments/footer :: footer}"로 불러옴 -->
<div class="footer" th:fragment="footer">
    <footer class="page-footer font-small cyan darken-3">
        <div class="footer-copyright text-center py-3">
            2026 Java Spring Boot Project
        </div>
    </footer>
</div>
</html>
```

> 네비게이션바 메뉴 추가·색상 변경 방법은 README의 "네비게이션바(상단 메뉴) 설정 방법"에 자세히 있습니다.

---

## 13. 메인 화면

### 13-1. 화면 파일

`templates` 폴더 바로 아래에 만듭니다.

📄 **`SBB1/src/main/resources/templates/index.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">


<!-- 메인 화면 ( / ) : layout.html 레이아웃을 사용하고, 아래 content 부분만 이 페이지 내용으로 채움 -->
<div layout:fragment="content" class="container my-3">
    <h2>스프링부트 - C반</h2>

</div>
</html>
```
📄 **`SBB1/src/main/resources/templates/temp.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">


<!-- 레이아웃 연습용 화면 ( /temp ) : 새 페이지를 만들 때 복사해서 쓰는 기본 틀 -->
<div layout:fragment="content" class="container my-3">
    <h2>제목</h2>

</div>
</html>
```

### 13-2. 공통 컨트롤러

`common/controller` 패키지에 만듭니다.

📄 **`SBB1/src/main/java/com/mysite/sbb1/common/controller/indexController.java`**

```java
package com.mysite.sbb1.common.controller;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 메인 화면 등 공통 페이지를 보여주는 컨트롤러
 * - @Controller : 메서드가 반환한 문자열을 templates 폴더의 html 파일 이름으로 보고 화면을 만들어 응답함
 */
@Controller
public class indexController {

    /**
     * GET /  (메인 화면)
     * "index" 반환 → templates/index.html 화면을 보여줌
     * (@ResponseBody를 붙이면 화면이 아니라 문자열 "index" 자체가 응답됨)
     */
    //@ResponseBody
    @GetMapping("/")
    public String index(){
        return "index";
    }

    /**
     * GET /temp  (레이아웃 연습용 화면)
     * "temp" 반환 → templates/temp.html 화면을 보여줌
     */
    @GetMapping("/temp")
    public String temp(){
        return "temp";
    }
}
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/common/controller/StartController.java`**

```java
package com.mysite.sbb1.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

/**
 * REST 방식 응답 연습용 컨트롤러 (1주차)
 * - @RestController : 화면(html) 대신 객체를 JSON 데이터로 바로 반환하는 컨트롤러
 */
@RestController
public class StartController {

    /**
     * GET /start
     * Point 객체를 반환하면 스프링이 자동으로 JSON으로 변환해서 응답함
     * 예) {"x":10.0,"y":20.0,...}
     */
    @GetMapping("/start")

    public Point start() {
        Point p = new Point(10,20);

        return p;
    }

}
```

- `StartController`는 JSON 응답 연습용입니다. `java.awt.Point`를 쓰기 때문에 import가 `java.awt.*`입니다.
- 클래스 이름 `indexController`는 원래 Java 관례상 대문자로 시작(`IndexController`)하는 게 맞지만, 저장소와 똑같이 맞추려고 그대로 두었습니다.

✅ **확인**: 서버를 다시 실행하고 브라우저로 열어 봅니다.
- http://localhost:8080 → 검은 메뉴바 + "스프링부트 - C반" + 아래 footer
- http://localhost:8080/temp → 같은 레이아웃에 "제목"
- http://localhost:8080/start → `{"x":10.0,"y":20.0}` (JSON 글자)

메뉴바가 디자인 없이 글자 목록처럼 보이면 11단계 Bootstrap 파일 위치를 확인하세요.
(메뉴의 "질문 목록"은 다음 단계를 끝내야 열립니다.)

---

## 14. 질문/답변 화면과 컨트롤러

### 14-1. 질문 화면 3개

`templates/question` 폴더에 만듭니다.

**질문 목록 + 페이징** (`/question/list`)

📄 **`SBB1/src/main/resources/templates/question/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">


<div layout:fragment="content" class="container my-3">

    <h2 class="mb-4">질문 리스트</h2>

    <!-- 질문 목록 화면 ( /question/list ) : 컨트롤러에서 넘겨준 paging(한 페이지 분량의 질문)을 표로 보여줌 -->
    <table class="table table-hover align-middle">
        <thead class="table-dark">
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성일시</th>
        </tr>
        </thead>

        <tbody>
        <!-- paging에 담긴 질문을 하나씩 꺼내 한 줄(tr)씩 출력. loop.index는 0부터 시작하는 반복 순서 -->
        <tr th:each="question, loop : ${paging}">
            <!-- 번호 = 전체 개수 - (현재 페이지 × 페이지당 개수) - 반복 순서 → 최신 글이 가장 큰 번호 -->
            <td th:text="${paging.getTotalElements - (paging.number * paging.size) - loop.index }"></td>
            <td>
                <a th:href="@{|/question/${question.id}|}"
                   th:text="${question.subject}">게시글 제목
                </a>
                <!-- 답변이 1개 이상이면 제목 옆에 답변 개수 배지 표시 -->
                <span th:if="${#lists.size(question.answerList) > 0}"
                      th:text="${#lists.size(question.answerList)}"
                      class="badge bg-primary"></span>
            </td>
            <td th:text="${#temporals.format(question.created, 'yyyy-MM-dd HH:mm')}">
                2026-10-07 10:00
            </td>
        </tr>
        </tbody>
    </table>

    <!-- 페이징 기능 추가 -->
    <!-- 코드는 먼저 paging 객체가 비어 있지 않은지 확인합니다. 이를 통해 페이징 데이터가 없는 경우 페이징 UI를 렌더링하지 않도록 합니다. -->
    <div th:if="${!paging.isEmpty()}">

        <!-- startPage와 endPage는 현재 페이지 번호(paging.number)를 기준으로 계산됩니다.
        startPage는 현재 페이지 번호를 10으로 나눈 뒤 10을 곱해 계산되며,
        endPage는 총 페이지 수(paging.totalPages)와 비교하여 적절한 값을 설정합니다.-->
        <th:block th:with="startPage=${(paging.number/10) * 10},
        endPage=(${(paging.totalPages == 0) ? 1 : (startPage + 9 < paging.totalPages ? startPage + 9 : paging.totalPages - 1)})">

            <!-- pagination, page-item, page-link 클래스 확인!-->
            <ul class="pagination justify-content-center">
                <!-- 처음 버튼: 첫 번째 페이지로 이동하는 버튼입니다. 현재 페이지가 첫 페이지(paging.number == 0)인 경우
                disabled 클래스를 추가하여 비활성화합니다.-->
                <li class="page-item" th:classappend="${paging.number == 0} ? 'disabled'">
                    <a class="page-link" href="javascript:void(0)" th:data-page="0">
                        <span>처음</span>
                    </a>
                </li>
                <!-- 이전 버튼: 이전 페이지로 이동하는 버튼입니다. 이전 페이지가 없는 경우(!paging.hasPrevious),
                disabled 클래스를 추가합니다. -->
                <li class="page-item" th:classappend="${!paging.hasPrevious} ? 'disabled'">
                    <a class="page-link" href="javascript:void(0)" th:data-page="${paging.number-1}">
                        <span>이전</span>
                    </a>
                </li>
                <!-- 페이지 번호 버튼: startPage부터 endPage까지의 페이지 번호를 반복 렌더링합니다. 현재 페이지는 active 클래스를 추가하여 강조합니다. -->
                <li th:each="page: ${#numbers.sequence(startPage, endPage)}"
                    th:classappend="${page == paging.number} ? 'active'" class="page-item">
                    <a th:text="${page + 1}" class="page-link" href="javascript:void(0)" th:data-page="${page}"></a>
                </li>
                <!-- 다음 버튼: 다음 페이지로 이동하는 버튼입니다. 다음 페이지가 없는 경우(!paging.hasNext), disabled 클래스를 추가합니다 -->
                <li class="page-item" th:classappend="${!paging.hasNext} ? 'disabled'">
                    <a class="page-link" href="javascript:void(0)" th:data-page="${paging.number+1}">
                        <span>다음</span>
                    </a>
                </li>
                <!-- 마지막 버튼: 마지막 페이지로 이동하는 버튼입니다. 현재 페이지가 마지막 페이지인 경우(paging.number == paging.totalPages-1), disabled 클래스를 추가합니다. -->
                <li class="page-item" th:classappend="${paging.number == paging.totalPages-1} ? 'disabled'">
                    <a class="page-link" href="javascript:void(0)" th:data-page="${paging.totalPages-1}">
                        <span>마지막</span>
                    </a>
                </li>
            </ul>

        </th:block>
    </div>
    <!-- 페이징 기능 종료 -->
    <!-- 페이지 버튼을 누르면 아래 스크립트가 page 값을 채운 뒤 이 폼을 GET /question/list?page=번호 로 전송 -->
    <form th:action="@{/question/list}" method="get" id="searchForm">
        <input type="hidden" id="page" name="page"
               th:value="${paging.number}">
    </form>
    <a th:href="@{/question/create}" class="btn btn-primary">질문 등록</a>

</div>
<script layout:fragment="script" type='text/javascript'>

    const page_elements = document.getElementsByClassName("page-link"); // 문서에서 page-link 클래스를 가진 모든 요소를 선택

    Array.from(page_elements).forEach(function (element) {
        element.addEventListener('click', function () { // forEach 메서드를 사용하여 각 page-link 요소에 대해 클릭 이벤트 리스너를 추가
            // HTML에서 개발자가 필요한 데이터를 요소에 저장할 수 있도록 정해 놓은 표준 : data-*
            // dataset 속성을 사용하면 data-* 속성에 접근 가능
            document.getElementById('page').value = this.dataset.page;  // 클릭 이벤트가 발생하면, 해당 버튼의 data-page 속성 값을 읽어와 숨겨진 폼 필드 page의 값으로 설정
            document.getElementById("searchForm").submit();
        });
    });
</script>
<!--<script layout:fragment="script" type="text/javascript">-->
<!--    const pag_elements = document.getElementsByTagName('page-link');-->
<!--    Array.from(pag_elements).forEach(function (element) {-->
<!--        element.addEventListener("click", function (event) { &lt;!&ndash;&ndash;&gt;-->
<!--            alert("11111");-->
<!--            document.getElementById("page").value = this.dataset.page; // data-page 필드 값을 페이지로 가져옴-->
<!--        })-->
<!--    })-->

<!--</script>-->
</html>
```

**질문 상세 + 답변 목록 + 답변 등록 폼** (`/question/{id}`)

📄 **`SBB1/src/main/resources/templates/question/detail.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">
<!-- 질문 상세 화면 ( /question/{id} )
     컨트롤러에서 넘겨준 question(질문 + 답변 목록)과 answerDto(답변 입력 폼)를 사용 -->

<!-- 입력값 검증 에러 메시지를 빨간 글씨로 표시하는 스타일
     layout.html <head> 안의 css 영역(layout:fragment="css")에 들어감 -->
<th:block layout:fragment="css">
    <style>
        .fledError{
            color:red;

        }
    </style>
</th:block>

<div layout:fragment="content" class="container my-3">
    <!-- 질문 상세 Card -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">질문 상세</h4>
        </div>

        <div class="card-body">
            <h3 class="card-title mb-3"
                th:text="${question.subject}">제목</h3>

            <hr>

            <div class="card-text"
                 style="white-space: pre-line;"
                 th:text="${question.content}">내용</div>
        </div>
    </div>

    <!-- 답변 목록 -->
    <div class="mb-4">
        <h5 class="mb-3"
            th:text="|${#lists.size(question.answerList)}개의 답변이 있습니다.|">
            0개의 답변이 있습니다.
        </h5>

        <!-- 답변이 없는 경우 -->
        <div th:if="${#lists.isEmpty(question.answerList)}"
             class="alert alert-light border">
            아직 등록된 답변이 없습니다.
        </div>

        <!-- 답변마다 개별 Card 생성 -->
        <div th:each="answer : ${question.answerList}"
             class="card shadow-sm mb-3">

            <div class="card-body">
                <p class="card-text mb-0"
                   style="white-space: pre-line;"
                   th:text="${answer.content}">
                    답변 내용
                </p>
            </div>

        </div>
    </div>

    <!-- 답변 등록 Card -->
    <div class="card shadow-sm">
        <div class="card-header bg-light">
            <h5 class="mb-0">답변 등록</h5>
        </div>

        <div class="card-body">
            <!-- 답변 등록 폼 : POST /answer/create/{질문id} 로 전송
                 th:object="${answerDto}" → 아래 *{content}는 answerDto.content를 뜻함 -->
            <form th:action="@{|/answer/create/${question.id}|}"
                  th:object="${answerDto}"
                  method="post">

                <div class="mb-3">
                    <label for="content" class="form-label">
                        답변 내용
                    </label>

                    <textarea class="form-control"
                              rows="5"
                              id="content"
                              name="content"
                              placeholder="답변을 입력해 주세요."
                              th:field="*{content}"
                              >
                    </textarea>
                    <!-- 내용이 비어 있으면 AnswerDto의 @NotEmpty 메시지를 표시 -->
                    <p th:if="${#fields.hasErrors('content')}" th:errors="*{content}" class="fledError">error</p>
                </div>

                <div class="text-end">
                    <button type="submit" class="btn btn-primary">
                        답변 등록
                    </button>
                </div>

            </form>
        </div>
    </div>

</div>
</html>
```

**질문 등록 폼** (`/question/create`)

📄 **`SBB1/src/main/resources/templates/question/inputForm.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/layout}">

<!-- 질문 등록 화면 ( GET /question/create )
     입력한 값은 POST /question/create 로 전송되고, 검증에 실패하면 이 화면이 에러 메시지와 함께 다시 보임 -->

<!-- 입력값 검증 에러 메시지를 빨간 글씨로 표시하는 스타일
     layout.html <head> 안의 css 영역(layout:fragment="css")에 들어감 -->
<th:block layout:fragment="css">
    <style>
        .fledError{
            color:red;

        }
    </style>
</th:block>

<div layout:fragment="content" class="container my-3">
    <h2>질문 등록</h2>
    <!-- th:object="${questionDto}" → 아래 *{subject}, *{content}는 questionDto의 필드를 뜻함
         th:field는 id, name, value를 자동으로 채워 주기 때문에 검증 실패 시 입력했던 값이 그대로 남음 -->
    <form th:action="@{/question/create}" th:object="${questionDto}" method="post">

        <!-- 제목 -->
        <div class="mb-3">
            <label for="subject" class="form-label">제목</label>
            <input type="text"
                   class="form-control"
                   id="subject"
                   name="subject"
                   th:field="*{subject}"
                   placeholder="제목을 입력하세요."
                   required>
            <!-- 제목이 비었거나 150자를 넘으면 QuestionDto에 적은 에러 메시지를 표시 -->
            <p th:if="${#fields.hasErrors('subject')}"
               class="fledError"
               th:errors="*{subject}">error</p>
        </div>

        <!-- 내용 -->
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea class="form-control"
                      id="content"
                      name="content"
                      rows="10"
                      placeholder="질문 내용을 입력하세요."
                      th:field="*{content}"
                      required></textarea>
            <p th:if="${#fields.hasErrors('content')}" th:errors="*{content}" class="fledError">error</p>
        </div>

        <!-- 질문 등록 버튼 -->
        <div class="text-end">
            <button type="submit" class="btn btn-primary">
                질문 등록
            </button>
        </div>

    </form>
</div>
</html>
```

### 14-2. 컨트롤러 2개

📄 **`SBB1/src/main/java/com/mysite/sbb1/question/controller/QuestionController.java`**

```java
package com.mysite.sbb1.question.controller;

import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.question.dto.QuestionDto;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 질문 관련 요청(목록, 상세, 등록)을 받아서 처리하는 컨트롤러
 * - @RequestMapping("/question") : 이 클래스의 모든 URL 앞에 /question이 붙음
 * - @Slf4j : log.info(...)로 콘솔에 로그를 찍을 수 있게 해줌
 * - Model : 컨트롤러에서 화면(html)으로 데이터를 넘길 때 사용하는 객체
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * GET /question/list?page=번호  (질문 목록 화면)
     * - page 파라미터가 없으면 0페이지(첫 페이지)를 보여줌
     * - 조회한 페이지 데이터를 "paging"이라는 이름으로 list.html에 넘김
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        Page<Question> paging = questionService.getList(page);
        log.info("====== paging : {}", paging);
        model.addAttribute("paging", paging);

        return "question/list";
    }

    /**
     * GET /question/{id}  (질문 상세 화면)
     * - URL의 {id} 값을 @PathVariable로 받아 해당 질문을 조회해서 detail.html에 넘김
     * - AnswerDto 파라미터 : 상세 화면 아래 답변 등록 폼(th:object="${answerDto}")에서
     *   사용할 빈 객체를 화면에 넘겨주기 위해 필요함
     */
    @GetMapping("/{id}")
    public String  detail(@PathVariable("id") Long id, AnswerDto answerDto, Model model) {
        log.info("======== id : " + id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    /**
     * GET /question/create  (질문 등록 폼 화면)
     * - QuestionDto 파라미터 : inputForm.html의 th:object="${questionDto}"에서 사용할 빈 객체
     */
    @GetMapping("/create")
    public String createForm(QuestionDto questionDto) {

        return "question/inputForm";
    }

    // 7주차 방식 : 폼 값을 @RequestParam으로 하나씩 받던 방법 (8주차에 DTO 방식으로 변경)
    /* @PostMapping("/create")
    public String createQuestion(@RequestParam("subject") String subject,
                                 @RequestParam("content") String content, Model model){
        log.info("======== subject : {}, content : {}", subject,content);
        questionService.createeQuestion(subject,content);
        return "redirect:/question/list";
    } */

    /**
     * POST /question/create  (질문 등록 처리)
     * - @Valid : QuestionDto의 @NotEmpty, @Size 검증을 실행
     * - BindingResult : 검증 결과가 담김. 반드시 @Valid 파라미터 바로 뒤에 와야 함
     * - 검증 실패 → 입력 화면으로 돌아가서 에러 메시지 표시
     * - 검증 성공 → 질문 저장 후 목록 화면으로 이동(redirect)
     */
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionDto questionDto,
                                 BindingResult bindingResult) {

        log.info("======== questionDto  : {}", questionDto);
        if(bindingResult.hasErrors()){
            return "question/inputForm";
        }

        questionService.createeQuestion(questionDto);
        return "redirect:/question/list";
    }
}
```
📄 **`SBB1/src/main/java/com/mysite/sbb1/answer/controller/AnswerController.java`**

```java
package com.mysite.sbb1.answer.controller;

import com.mysite.sbb1.answer.dto.AnswerDto;
import com.mysite.sbb1.answer.service.AnswerService;
import com.mysite.sbb1.question.entity.Question;
import com.mysite.sbb1.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 답변 등록 요청을 처리하는 컨트롤러
 * - 답변을 달 질문을 찾아야 하므로 QuestionService도 함께 주입받음
 */
@Controller //컨트롤러 역활 지정
@Slf4j
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    /**
     * POST /answer/create/{id}  (답변 등록 처리, {id}는 질문 번호)
     * - 질문 상세 화면의 답변 등록 폼에서 호출됨
     * - 검증 실패(내용이 비어 있음) → 질문 정보를 다시 담아서 상세 화면으로 돌아가 에러 메시지 표시
     * - 검증 성공 → 답변 저장 후 해당 질문 상세 화면으로 이동(redirect)
     */
    @PostMapping("/answer/create/{id}")//post방식이라
    public String createAnswer(@PathVariable("id") Long id,
                               @Valid AnswerDto answerDto, BindingResult bindingResult,
                               Model model){
        log.info("======= id : {}, answerDto : {}", id, answerDto);
        Question question = questionService.getQuestion(id);

        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "question/detail";
        }

        answerService.createAnswer(question, answerDto);
        return "redirect:/question/" + id;
    }
}
```

- 컨트롤러가 `return "question/list"`라고 하면 `templates/question/list.html`을 찾습니다. 이름이 한 글자라도 다르면 화면을 못 찾는 에러가 납니다.

✅ **확인**: 서버를 다시 실행하고 아래 순서대로 해 봅니다.

1. 메뉴의 **질문 목록** 클릭 (http://localhost:8080/question/list) → 빈 표와 **질문 등록** 버튼
2. **질문 등록** → 제목/내용을 **비운 채로** 등록 → 빨간 글씨 "제목은 필수 항목입니다." 등이 보이면 검증 성공
   (입력칸에 `required`가 있어서 브라우저가 먼저 막을 수도 있습니다. 그래도 정상입니다.)
3. 제목/내용을 쓰고 등록 → 목록으로 돌아오고 방금 쓴 질문이 보임
4. 제목 클릭 → 상세 화면 → 답변 쓰고 **답변 등록** → "1개의 답변이 있습니다."와 답변 내용 표시
5. 목록으로 돌아가면 제목 옆에 파란 숫자 배지(답변 개수) 표시
6. Workbench에서 `SELECT * FROM question; SELECT * FROM answer;` → 방금 넣은 데이터 확인

**여기까지 되면 프로젝트 완성입니다.** 🎉

---

## 15. 테스트 코드

테스트 코드는 `src/test/java/com/mysite/sbb1` 아래에 만듭니다. 화면 없이 DB 기능만 따로 확인하는 코드입니다.

### 15-1. 파일 만들기

`Sbb1ApplicationTests.java`는 자동으로 만들어져 있습니다.

📄 **`SBB1/src/test/java/com/mysite/sbb1/Sbb1ApplicationTests.java`**

```java
package com.mysite.sbb1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 기본 테스트 : 스프링 애플리케이션이 에러 없이 실행(빈 등록, DB 연결 등)되는지 확인
 */
@SpringBootTest
class Sbb1ApplicationTests {

    @Test
    void contextLoads() {
    }

}
```

`src/test/java/com/mysite/sbb1`에 패키지 `question.repository`를 만들고 아래 파일을 만듭니다.

📄 **`SBB1/src/test/java/com/mysite/sbb1/question/repository/QuestionRepositoryTest.java`**

```java
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
```

패키지 `answer.reoisitory`를 만들고 아래 파일을 만듭니다. (`reoisitory`는 repository의 오타지만, 파일 첫 줄의 `package`와 폴더 이름이 같기만 하면 동작합니다.)

📄 **`SBB1/src/test/java/com/mysite/sbb1/answer/reoisitory/AnswerRespositoryTest.java`**

```java
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
```

### 15-2. 실행 순서 (중요)

이 테스트들은 **실제 MySQL DB에 데이터를 넣고**, "id가 1번인지", "개수가 2개인지"처럼 **DB에 이미 있는 데이터를 기준으로 검사**합니다.
그래서 DB가 비어 있는 상태에서, 아래 순서대로 **메서드를 하나씩** 실행해야 모두 통과합니다.
(클래스 전체를 한 번에 실행하면 순서가 보장되지 않아 실패할 수 있습니다.)

**1) DB 비우기** — 14단계에서 웹으로 넣은 데이터가 있으면 id가 어긋나므로 지우고 다시 만듭니다.

```sql
DROP DATABASE sbb_c;
CREATE DATABASE sbb_c DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

그다음 서버를 한 번 실행했다가 멈추면 빈 테이블이 다시 만들어집니다. (테스트를 실행해도 테이블이 만들어지므로 이 단계는 생략해도 됩니다.)

**2) 메서드 옆 초록색 ▶를 눌러 하나씩 실행**

| 순서 | 파일 | 메서드 | 확인하는 내용 |
|---|---|---|---|
| 1 | `Sbb1ApplicationTests` | `contextLoads` | 앱이 정상 실행되는지 |
| 2 | `QuestionRepositoryTest` | `testSave` | 질문 2개 저장 (id 1, 2) — **딱 한 번만** 실행 |
| 3 | `QuestionRepositoryTest` | `testFindAll` | 전체 질문이 2개인지 |
| 4 | `QuestionRepositoryTest` | `testFindById` | 1번 질문 조회 |
| 5 | `QuestionRepositoryTest` | `testFindBySubject` | 제목으로 조회 |
| 6 | `QuestionRepositoryTest` | `testfindBySubjectAndContent` | 제목 + 내용으로 조회 |
| 7 | `QuestionRepositoryTest` | `testfindBySubjectLike` | 제목에 "sbb"가 들어간 질문 조회 |
| 8 | `QuestionRepositoryTest` | `testUpdate` | 2번 질문 제목 수정 |
| 9 | `AnswerRespositoryTest` | `testFindById` | 1번 질문에 답변 저장 (답변 id 1) |
| 10 | `AnswerRespositoryTest` | `testGetAnswerList` | 1번 질문의 답변이 1개인지 |
| 11 | `QuestionRepositoryTest` | `testManyData` | 페이징 확인용 질문 300개 생성 |

- `testSave`를 두 번 실행하면 질문이 4개가 되어 `testFindAll`이 실패합니다. 그럴 때는 1)부터 다시 하면 됩니다.
- `test()`는 콘솔에 글자만 찍는 연습용이라 아무 때나 실행해도 됩니다.

✅ **확인**: 11번까지 실행한 뒤 서버를 켜고 질문 목록에 들어가면, 한 페이지에 10개씩 나오고 아래에 **처음 / 이전 / 1 2 3 … / 다음 / 마지막** 페이지 버튼이 보입니다.

---

## 16. 자주 나는 에러와 해결 방법

| 에러 메시지 (콘솔) | 원인 | 해결 |
|---|---|---|
| `Communications link failure` | MySQL이 꺼져 있음 | 윈도우 `서비스`에서 `MySQL80`(버전에 따라 `MySQL84` 등) 시작 / Workbench로 접속되는지 확인 |
| `Access denied for user 'root'@'localhost'` | 비밀번호가 다름 | `application.yml`의 `password`를 내 MySQL 비밀번호로 수정 |
| `Unknown database 'sbb_c'` | DB를 안 만듦 | 1단계 `CREATE DATABASE` 실행 |
| `Port 8080 was already in use` | 서버가 이미 켜져 있음 | IntelliJ 아래 Run 창에서 이전 실행을 ■로 멈추기 / 또는 `server.port`를 다른 번호로 |
| `cannot find symbol` (`builder()`, `getSubject()` 등) | Lombok이 동작 안 함 | 3단계 4번 **Enable annotation processing** 확인 후 `Build` → `Rebuild Project` |
| `package jakarta.validation... does not exist` | validation 의존성 없음 / Gradle 새로고침 안 함 | 4단계 build.gradle 확인 후 🐘 Load Gradle Changes |
| `TemplateInputException: Error resolving template [...]` | 컨트롤러가 반환한 이름의 html이 없음 | `templates` 아래 폴더/파일 이름 확인 (예: `question/inputForm.html` 대소문자까지) |
| 브라우저에 `Whitelabel Error Page` 404 | 그 주소를 처리하는 컨트롤러가 없음 | 주소 오타 / `@GetMapping` 경로 확인 |
| `EntityNotFoundException: 해당 데이터 없음` | 없는 질문 번호로 접속 | 목록에서 제목을 눌러 들어가기 |
| `Property or field '...' cannot be found` | html에서 쓴 필드 이름이 엔티티에 없음 | 예: `answer.content`와 `Answer` 클래스 필드 이름이 같은지 확인 |
| 화면이 디자인 없이 글자만 나옴 | Bootstrap CSS를 못 찾음 | `static/css/bootstrap.min.css` 위치와 이름 확인, 브라우저 F12 → Network에서 404 확인 |
| 메뉴 ☰ / 드롭다운이 안 열림 | Bootstrap JS 문제 | `static/js/bootstrap.bundle.min.js`(bundle 버전)인지 확인 |
| 한글이 `???`로 저장됨 | DB 문자셋 | 1단계처럼 `utf8mb4`로 DB를 만들었는지 확인 |
| yml 수정 후 실행이 안 됨 | 들여쓰기 오류 | Tab 대신 띄어쓰기 2칸, 5단계 코드와 칸 수 비교 |

---

## 17. GitHub에 올리기 (선택)

1. GitHub에서 새 저장소를 만듭니다. (README 등은 추가하지 않고 빈 저장소로)
2. 프로젝트 폴더의 **상위 폴더**(이 저장소는 `SBB1` 폴더를 담고 있는 `Springboot` 폴더가 루트)에서 터미널을 열고 실행합니다.

```bash
git init
```

```bash
git add .
```

```bash
git commit -m "first commit"
```

```bash
git branch -M main
```

```bash
git remote add origin https://github.com/내아이디/저장소이름.git
```

```bash
git push -u origin main
```

- `build/`, `.gradle/` 같은 빌드 결과물은 `SBB1/.gitignore`에 적혀 있어서 자동으로 빠집니다.
- 이후 수정할 때는 `git add .` → `git commit -m "메시지"` → `git push` 순서로 올리면 됩니다.

---

## 18. 최종 파일 체크리스트

아래 파일이 모두 있으면 지금 저장소와 같은 상태입니다.

```
SBB1/
├── build.gradle                                       (4단계)
├── settings.gradle                                    (자동 생성)
└── src/
    ├── main/
    │   ├── java/com/mysite/sbb1/
    │   │   ├── Sbb1Application.java                   (자동 생성)
    │   │   ├── common/controller/
    │   │   │   ├── indexController.java               (13단계)
    │   │   │   └── StartController.java               (13단계)
    │   │   ├── question/
    │   │   │   ├── entity/Question.java               (7단계)
    │   │   │   ├── repository/QuestionRepository.java (8단계)
    │   │   │   ├── dto/QuestionDto.java               (9단계)
    │   │   │   ├── service/QuestionService.java       (10단계)
    │   │   │   └── controller/QuestionController.java (14단계)
    │   │   └── answer/
    │   │       ├── entity/Answer.java                 (7단계)
    │   │       ├── repository/AnswerRespository.java  (8단계)
    │   │       ├── dto/AnswerDto.java                 (9단계)
    │   │       ├── service/AnswerService.java         (10단계)
    │   │       └── controller/AnswerController.java   (14단계)
    │   └── resources/
    │       ├── application.yml                        (5단계, application.properties는 삭제)
    │       ├── logback-spring.xml                     (5단계)
    │       ├── static/
    │       │   ├── css/bootstrap.min.css              (11단계)
    │       │   └── js/bootstrap.bundle.min.js         (11단계)
    │       └── templates/
    │           ├── index.html                         (13단계)
    │           ├── temp.html                          (13단계)
    │           ├── layouts/layout.html                (12단계)
    │           ├── fragments/header.html              (12단계)
    │           ├── fragments/footer.html              (12단계)
    │           └── question/
    │               ├── list.html                      (14단계)
    │               ├── detail.html                    (14단계)
    │               └── inputForm.html                 (14단계)
    └── test/java/com/mysite/sbb1/
        ├── Sbb1ApplicationTests.java                  (자동 생성)
        ├── question/repository/QuestionRepositoryTest.java   (15단계)
        └── answer/reoisitory/AnswerRespositoryTest.java      (15단계)
```
