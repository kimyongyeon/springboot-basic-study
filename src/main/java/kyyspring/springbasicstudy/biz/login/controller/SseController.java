package kyyspring.springbasicstudy.biz.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/sse")
@Slf4j
@CrossOrigin(origins = "*")
public class SseController {
    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String userId) {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> userEmitters.get(userId).remove(emitter));
        emitter.onTimeout(() -> userEmitters.get(userId).remove(emitter));

        // 연결 성공시 더미 데이터 한번 보내주기
        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE connected"));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendLogoutEvent(String userId, String excludeDeviceId) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("FORCE_LOGOUT")
                        .data("remote logout"));
                emitter.complete(); // 한 번 보내고 종료할 수도 있음
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String userId, @RequestParam String deviceId) {
        // 1. 토큰 무효화
//        tokenService.invalidate(userId, deviceId);
        log.debug("logout: userId={}, deviceId={}", userId, deviceId);

        // 2. SSE 이벤트 전송
        this.sendLogoutEvent(userId, deviceId);

        return ResponseEntity.ok().build();
    }
}
