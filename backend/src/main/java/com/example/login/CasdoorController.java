package com.example.login;

import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth/casdoor")
public class CasdoorController {

    private static final String STATE = "casdoor-sso";

    private final AuthService casdoorAuthService;

    @Value("${app.frontend-origin:http://localhost:5173}")
    private String frontendOrigin;

    public CasdoorController(AuthService casdoorAuthService) {
        this.casdoorAuthService = casdoorAuthService;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String callbackUrl = frontendOrigin + "/api/auth/casdoor/callback";
        String authorizeUrl = casdoorAuthService.getSigninUrl(callbackUrl, STATE);
        response.sendRedirect(authorizeUrl + "&nonce=casdoor-nonce");
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         HttpServletResponse response,
                         HttpSession session) throws IOException {
        try {
            String token = casdoorAuthService.getOAuthToken(code, STATE);
            User user = casdoorAuthService.parseJwtToken(token);

            session.setAttribute("casdoorUser", user.owner + "/" + user.name);

            String redirect = frontendOrigin + "/?casdoor=success&username="
                    + URLEncoder.encode(user.name, StandardCharsets.UTF_8);
            response.sendRedirect(redirect);
        } catch (Exception e) {
            response.sendRedirect(frontendOrigin + "/?casdoor=error");
        }
    }
}
