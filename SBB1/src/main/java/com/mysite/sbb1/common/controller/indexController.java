package com.mysite.sbb1.common.controller;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class indexController {
    //@ResponseBody
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
