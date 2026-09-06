package com.example.login;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    public static final String SESSION_USER = "loginUser";

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");

        if (userRepository.authenticate(username, password)) {
            session.setAttribute(SESSION_USER, username);
            String role = userRepository.findRoleByUsername(username).orElse("user");
            return ApiResponse.success(Map.of("username", username, "role", role));
        }
        return ApiResponse.error(1001, "用户名或密码错误");
    }

    @GetMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null);
    }
}
