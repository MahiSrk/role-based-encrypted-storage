package com.reks.controller;

import com.reks.dto.RegisterRequest;
import com.reks.dto.LoginRequest;
import com.reks.model.User;
import com.reks.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterRequest registerRequest, Model model) {
        try {
            User user = authService.register(registerRequest);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginRequest loginRequest, Model model, HttpServletRequest request) {
        try {
            String token = authService.login(loginRequest);
            // (Optional) Store token in session or cookie
            request.getSession().setAttribute("jwt", token);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}

