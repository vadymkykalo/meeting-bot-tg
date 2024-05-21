package com.vadymkykalo.meetingbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class OAuth2CallbackController {

    @GetMapping("/oauth2callback")
    public RedirectView oauth2Callback(@RequestParam("code") String code) {
        return new RedirectView("/");
    }
}
