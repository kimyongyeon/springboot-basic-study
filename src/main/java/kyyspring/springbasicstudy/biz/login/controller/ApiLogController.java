package kyyspring.springbasicstudy.biz.login.controller;

import kyyspring.springbasicstudy.biz.login.domain.ApiLog;
import kyyspring.springbasicstudy.biz.login.service.ApiLogService;
import kyyspring.springbasicstudy.biz.sample.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class ApiLogController {

    private final ApiLogService apiLogService;
    private final SampleService service;

    /**
     * 예시 API: 사용자 등록 요청을 로그와 함께 처리
     */
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> requestBody) {

        service.init();

        // 트랜잭션 ID 생성 (요청-응답 구분용)
        String txId = UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();



        // 요청 로그 저장
        ApiLog log = apiLogService.saveRequest(
                txId,
                "userRegister",
                "POST",
                "/api/log/user/register",
                requestBody
        );

        // 실제 비즈니스 로직 (예: 사용자 생성)
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("userId", "kim123");
        responseBody.put("timestamp", LocalDateTime.now().toString());

        // 응답 처리 시간 계산
        int elapsedMs = (int) (System.currentTimeMillis() - startTime);

        // 응답 로그 업데이트
        apiLogService.updateResponse(txId, responseBody, 200, elapsedMs);

        // 응답 반환
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 로그 상세 조회 (트랜잭션 ID 기준)
     */
    @GetMapping("/{txId}")
    public ResponseEntity<?> getLogDetail(@PathVariable String txId) {
        ApiLog log = apiLogService.getByTxId(txId);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("txId", log.getTxId());
        result.put("apiName", log.getApiName());
        result.put("req_log", log.getReqLog());
        result.put("res_log", log.getResLog());
        result.put("statusCode", log.getStatusCode());
        result.put("elapsedMs", log.getElapsedMs());
        result.put("createdAt", log.getCreatedAt());
        result.put("completedAt", log.getCompletedAt());
        return ResponseEntity.ok(result);
    }

    /**
     * 최근 로그 리스트 조회
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentLogs() {
        return ResponseEntity.ok(apiLogService.getRecentLogs());
    }
}