package com.cothomp.room_reservation.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final String USERNAME = "admin";
    private final String PASSWORD = "password";

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    } 

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model){
        if (USERNAME.equals(username) && PASSWORD.equals(password)){
            session.setAttribute("loggedInUser", username);
            return "redirect:/rooms";
        } else {
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }
}
