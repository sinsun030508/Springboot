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
