package com.nithin.onlinebank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nithin.onlinebank.model.User;
import com.nithin.onlinebank.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ================= LOGIN PAGE =================
    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    // ================= LOGIN PROCESS =================
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        session.setAttribute("loggedInUser", user);

        return "redirect:/dashboard";
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }

        return "dashboard";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ================= REGISTER PROCESS (IMPORTANT FIX) =================
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {

        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        User user = new User(username, password);
        userRepository.save(user);

        return "redirect:/";   // go to login after register
    }
}