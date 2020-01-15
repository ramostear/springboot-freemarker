package com.ramostear.springbootfreemarker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName DirectiveController
 * @Description TODO
 * @Author ramostear
 * @Date 2020/1/15 0015 20:13
 * @Version 1.0
 **/
@Controller
public class DirectiveController {

    @GetMapping("/hello/world")
    public String helloWorld(){
        return "HelloWorld";
    }

    @GetMapping("/hello/china")
    public String helloChina(){
        return "HelloChina";
    }
}
