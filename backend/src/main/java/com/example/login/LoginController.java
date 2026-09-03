package com.example.login;

import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private final JdbcTemplate jdbcTemplate;

    public LoginController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?",
                Integer.class, username, password);

        if (count != null && count > 0) {
            session.setAttribute(SESSION_USER, username);
            return ApiResponse.success(Map.of("username", username));
        }
        return ApiResponse.error(1001, "用户名或密码错误");
    }

    @GetMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null);
    }
}
