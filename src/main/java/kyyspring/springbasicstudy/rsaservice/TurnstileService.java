package kyyspring.springbasicstudy.rsaservice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnstileService {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final WebClient webClient = WebClient.builder().build();

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public boolean verify(String token, String remoteIp) {
        if (token == null || token.isBlank()) {
            log.warn("Turnstile token is null or blank");
            return false;
        }

        Mono<TurnstileResponse> responseMono = webClient.post()
                .uri(VERIFY_URL)
                .body(BodyInserters
                                .fromFormData("secret", secretKey)
                                .with("response", token)
                        // .with("remoteip", remoteIp != null ? remoteIp : "")
                )
                .retrieve()
                .bodyToMono(TurnstileResponse.class);

        TurnstileResponse response = responseMono.block();

        if (response == null) {
            log.warn("Turnstile verification response is null");
            return false;
        }

        log.info("Turnstile verify result: success={}, errorCodes={}",
                response.isSuccess(), response.getErrorCodes());

        return response.isSuccess();
    }

    @Data
    public static class TurnstileResponse {
        private boolean success;
        private String challenge_ts;
        private String hostname;
        private List<String> errorCodes;
        private String action;
        private String cdata;
    }
}