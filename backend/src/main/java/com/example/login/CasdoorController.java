package com.example.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/casdoor")
public class CasdoorController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${casdoor.endpoint:http://localhost:8000}")
    private String casdoorEndpoint;

    @Value("${casdoor.client-id:weblogin-app}")
    private String clientId;

    @Value("${casdoor.client-secret:weblogin-secret-2026}")
    private String clientSecret;

    @Value("${casdoor.redirect-uri:http://localhost:8080/api/auth/casdoor/callback}")
    private String redirectUri;

    @Value("${app.frontend-origin:http://localhost:5173}")
    private String frontendOrigin;

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String authorizeUrl = UriComponentsBuilder.fromHttpUrl(casdoorEndpoint + "/login/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "read")
                .queryParam("state", "casdoor-sso")
                .queryParam("nonce", "casdoor-nonce")
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletResponse response,
                         HttpSession session) throws IOException {
        try {
            String token = exchangeCodeForToken(code);
            Map<String, Object> userInfo = fetchUserInfo(token);
            String name = String.valueOf(userInfo.getOrDefault("name", ""));
            String owner = String.valueOf(userInfo.getOrDefault("owner", ""));

            session.setAttribute("casdoorUser", owner + "/" + name);

            String redirect = UriComponentsBuilder.fromHttpUrl(frontendOrigin)
                    .queryParam("casdoor", "success")
                    .queryParam("username", name)
                    .build()
                    .encode()
                    .toUriString();
            response.sendRedirect(redirect);
        } catch (Exception e) {
            response.sendRedirect(frontendOrigin + "/?casdoor=error");
        }
    }

    private String exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        form.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                casdoorEndpoint + "/api/login/oauth/access_token", request, Map.class);
        return String.valueOf(response.getBody().get("access_token"));
    }

    private Map<String, Object> fetchUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                casdoorEndpoint + "/api/get-account", org.springframework.http.HttpMethod.GET,
                request, Map.class);
        Map<String, Object> body = response.getBody();
        return (Map<String, Object>) body.get("data");
    }
}
