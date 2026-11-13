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
