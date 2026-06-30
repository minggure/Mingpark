package com.example.mingpark.service;

import com.example.mingpark.dto.KakaoTokenResponseDto;
import com.example.mingpark.dto.KakaoUserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {
    // 우리 서버가 다른 서버에 HTTP 요청을 보낼 때 사용하는 도구
    private final RestClient restClient = RestClient.create();
    // 이 값들은 환경변수에서 가져옴
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getKakaoLoginUrl() {
        // 카카오 REST API 키가 제대로 설정되어 있는지 먼저 검사
        validateKakaoClientId();

        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .build()
                .toUriString();
    }

    public KakaoTokenResponseDto getToken(String code) {
        validateKakaoClientId();
        // 카카오 토큰 발급 API에 보낼 데이터를 담을 객체
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        if (clientSecret != null && !clientSecret.isBlank()) {
            formData.add("client_secret", clientSecret);
        }

        try {
            // 실제로 카카오 서버에 톸큰 발급 요청 보내는 코드
            Map<String, Object> response = restClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(Map.class);
            String accessToken = (String) response.get("access_token");
            String tokenType = (String) response.get("token_type");
            String refreshToken = (String) response.get("refresh_token");
            Integer expiresInValue = null;
            Object expiresIn = response.get("expires_in");

            if (expiresIn instanceof Number number) {
                expiresInValue = number.intValue();
            }
            // access token이 없는지 확인
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalStateException("카카오 access_token 응답이 비어 있습니다.");
            }

            return new KakaoTokenResponseDto(accessToken, tokenType, refreshToken, expiresInValue);
        } catch (RestClientResponseException e) {
            log.warn("Kakao token request failed. status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
    // 카카오에서 받은 access token을 사용해서 사용자의 카카오 정보를 가져옴
    public KakaoUserResponseDto getUserInfo(String accessToken) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            Long kakaoId = null;
            Object id = response.get("id");
            if (id instanceof Number number) {
                kakaoId = number.longValue();
            }

            KakaoUserResponseDto.KakaoAccount kakaoAccount = null;
            Object kakaoAccountValue = response.get("kakao_account");
            if (kakaoAccountValue instanceof Map<?, ?> kakaoAccountMap) {
                String email = (String) kakaoAccountMap.get("email");

                KakaoUserResponseDto.Profile profile = null;
                Object profileValue = kakaoAccountMap.get("profile");
                if (profileValue instanceof Map<?, ?> profileMap) {
                    profile = new KakaoUserResponseDto.Profile((String) profileMap.get("nickname"));
                }

                kakaoAccount = new KakaoUserResponseDto.KakaoAccount(email, profile);
            }

            if (kakaoId == null) {
                throw new IllegalStateException("카카오 사용자 id 응답이 비어 있습니다.");
            }

            return new KakaoUserResponseDto(kakaoId, kakaoAccount);
        } catch (RestClientResponseException e) {
            log.warn("Kakao user info request failed. status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private void validateKakaoClientId() {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("KAKAO_REST_API_KEY 환경변수를 설정해 주세요.");
        }
    }
}
