package com.vadymkykalo.meetingbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * you are using Google API Client library for OAuth2 authorization,
 * you don't need to create your own controller to handle OAuth2 callback.
 * The library handles this process itself. However, if you need to log or do additional actions
 * when receiving a callback, you can leave the controller.
 */
//@Slf4j
//@Controller
public class OAuth2CallbackController {

//    @GetMapping("/oauth2callback")
//    public RedirectView oauth2Callback(@RequestParam("code") String code) {
//        log.info("OAuth2 code received: {}", code);
//        return new RedirectView("/");
//    }
}
