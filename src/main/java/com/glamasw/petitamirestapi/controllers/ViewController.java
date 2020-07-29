package com.glamasw.petitamirestapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


public class ViewController {

/*  Web AutoConfiguration is executed when Spring Boot identifies a class with the @Controller annotation.
    Spring Boot will automatically add static web resources located within any of the following directories:
            /META-INF/resources/
            /resources/
            /static/
            /public/
    https://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot*/
    @RequestMapping(value = {"/","/cliente", "/cliente/**", "/pet/**"})
    public String getIndex(HttpServletRequest request) {
        return "forward:/index.html";
    }

//    This controller simply redirects everything to index.html, allowing react and react-router to work its magic.
//    @RequestMapping(value = {"/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}"})
//    public String getIndex(HttpServletRequest request) {
//        return "index.html";
//    }
}
