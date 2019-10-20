package com.gabizou.cameraiq.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/")
    String home() {
        return "Welcome to CameraIQ Assessmenet Request Demo";
    }


}
