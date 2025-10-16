package com.cothomp.room_reservation.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        String user = (String) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", user);
        return "index";
    }
}

