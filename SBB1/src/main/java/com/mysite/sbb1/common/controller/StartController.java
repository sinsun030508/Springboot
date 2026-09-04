package com.mysite.sbb1.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class StartController {

    @GetMapping("/start")
    public Point start() {
        Point p = new Point(10,20);

        return p;
    }

}
