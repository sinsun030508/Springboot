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
