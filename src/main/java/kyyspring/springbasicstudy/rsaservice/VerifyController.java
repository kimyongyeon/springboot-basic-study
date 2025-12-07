package kyyspring.springbasicstudy.rsaservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class VerifyController {

    private final TurnstileService turnstileService;

    /**
     * 프론트에서 캡차 토큰을 보내면 여기서 검증
     */
    @PostMapping("/api/verify")
    public ResponseEntity<VerifyResponse> verify(
            @RequestBody VerifyRequest request,
            HttpServletRequest httpServletRequest) {

        String remoteIp = httpServletRequest.getRemoteAddr();
        boolean success = turnstileService.verify(request.getToken(), remoteIp);

        VerifyResponse resp = new VerifyResponse();
        resp.setSuccess(success);
        resp.setMessage(success ? "Captcha OK" : "Captcha Failed");

        return ResponseEntity.ok(resp);
    }

    @Data
    public static class VerifyRequest {
        private String token;
    }

    @Data
    public static class VerifyResponse {
        private boolean success;
        private String message;
    }
}