package com.MiTi.MiTi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class SpotifyController {

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.spotify.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SpotifyController(WebClient.Builder webClientBuilder, OAuth2AuthorizedClientService authorizedClientService) {
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com").build();
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String scope = "user-read-playback-state user-modify-playback-state streaming app-remote-control user-read-currently-playing";
        String state = UUID.randomUUID().toString(); // CSRF 보호를 위한 상태 토큰 생성
        String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8.toString());

        String url = "https://accounts.spotify.com/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&scope=" + encodedScope
                + "&redirect_uri=" + redirectUri
                + "&state=" + state;

        // 상태 토큰을 세션에 저장하여 나중에 확인할 수 있도록 합니다.
        response.addHeader("X-State-Token", state);

        response.sendRedirect(url);
    }

    @GetMapping("/api/spotify-token")
    public ResponseEntity<Map<String, String>> getSpotifyAccessToken(@RequestParam String code) {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        String tokenResponse = webClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            System.out.println("Error occurred: " + clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Failed to retrieve access token"));
                        })
                .bodyToMono(String.class)
                .block();

        if (tokenResponse == null) {
            throw new RuntimeException("Failed to retrieve token response");
        }

        String accessToken = extractAccessToken(tokenResponse);

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Failed to extract access token");
        }

        return ResponseEntity.ok(Collections.singletonMap("access_token", accessToken));
    }

    @GetMapping("/login/oauth2/code/spotify")
    public String handleSpotifyCallback(@RequestParam String code, @RequestParam String state, HttpServletResponse response, Model model) throws IOException {
        // 상태 토큰 확인 (CSRF 보호)
        String sessionState = response.getHeader("X-State-Token");
        if (sessionState == null || !sessionState.equals(state)) {
            throw new RuntimeException("Invalid state token");
        }

        String tokenResponse = webClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            System.out.println("Error occurred: " + clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Failed to retrieve access token"));
                        })
                .bodyToMono(String.class)
                .block();

        if (tokenResponse == null) {
            throw new RuntimeException("Failed to retrieve token response");
        }

        String accessToken = extractAccessToken(tokenResponse);

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Failed to extract access token");
        }

        // 액세스 토큰을 로그에 출력하여 디버깅 (생산 환경에서는 사용하지 않도록 주의)
        System.out.println("Extracted Access Token: " + accessToken);

        // 액세스 토큰을 모델에 추가
        model.addAttribute("accessToken", accessToken);

        // 템플릿 이름이 아닌 URL로 리다이렉트
        return "redirect:/index"; // index.html로 리디렉션
    }

    @GetMapping("/api/get-token")
    public Map<String, String> getToken(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);

        return response;
    }

    private String extractAccessToken(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(responseBody);
            if (jsonNode.has("access_token")) {
                return jsonNode.get("access_token").asText();
            } else {
                throw new RuntimeException("Access token not found in response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse token response", e);
        }
    }
}
