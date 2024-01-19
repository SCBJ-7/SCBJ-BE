package com.yanolja.scbj.global.config.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.yanolja.scbj.domain.member.service.MailService;
import com.yanolja.scbj.global.config.RetryConfig;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Message;
import com.yanolja.scbj.global.exception.ErrorCode;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FCMService {

    private final MailService mailService;
    private final FCMTokenRepository fcmTokenRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fcm.request-url}")
    private String fcmRequestURL;

    @Value("${fcm.credential-path}")
    private String credentialPath;

    @Value("${fcm.scope}")
    private String scope;

    public FCMService(FCMTokenRepository fcmTokenRepository,
        RestTemplate restTemplate, ObjectMapper objectMapper, MailService mailService) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.mailService = mailService;
    }

    @Async
    @Retryable(
        retryFor = FirebaseServerException.class,
        maxAttempts = RetryConfig.MAX_ATTEMPTS,
        backoff = @Backoff(delay = RetryConfig.MAX_DELAY)
    )
    public void sendMessageTo(final String email, final Data data) {

        if (!hasKey(email)) {
            return;
        }

        final String message = makeMessage(getToken(email), data);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());

        final HttpEntity<String> httpEntity = new HttpEntity<>(message, httpHeaders);


        try {
            final ResponseEntity<String> exchange = restTemplate.exchange(
                fcmRequestURL,
                HttpMethod.POST,
                httpEntity,
                String.class
            );

            if (exchange.getStatusCode().isError()) {
                throw new FirebaseServerException(ErrorCode.FIREBASE_SERVER_ERROR);
            }

        } catch (HttpClientErrorException ex) {
            throw new FirebaseServerException(ErrorCode.FIREBASE_SERVER_ERROR);
        }

    }
    @Recover
    public void sendEmailForFailureToSendAlarm(final FirebaseServerException e, final String email, final Data data) {
        mailService.sendEmail(email, data.getTitle(), data.getMessage());
    }

    private String makeMessage(final String targetToken, final Data data) {
        final Data messageData = new Data(data.getTitle(), data.getMessage(), data.getDate());
        final Message message = new Message(messageData, targetToken);

        final FCMRequest fcmMessage = new FCMRequest(false, message);

        try {
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAccessToken() {
        try {
            final GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(
                    (credentialPath)).getInputStream())
                .createScoped(List.of(scope));

            googleCredentials.refreshIfExpired();

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void saveToken(String email, String token) {
        fcmTokenRepository.saveToken(email, token);
    }

    public void deleteToken(String email) {
        if(hasKey(email)) {
            fcmTokenRepository.deleteToken(email);
        }
    }


    private String getToken(String email) {
        return fcmTokenRepository.getToken(email);
    }

    private boolean hasKey(String email) {
        return fcmTokenRepository.hasKey(email);
    }
}