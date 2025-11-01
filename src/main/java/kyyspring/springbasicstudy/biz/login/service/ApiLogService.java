package kyyspring.springbasicstudy.biz.login.service;

import kyyspring.springbasicstudy.biz.login.domain.ApiLog;
import kyyspring.springbasicstudy.biz.login.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiLogService {

    private final ApiLogRepository apiLogRepository;

    public ApiLog saveRequest(String txId, String apiName, String method, String endpoint, Map<String, Object> reqLog) {
        ApiLog log = ApiLog.builder()
                .txId(txId)
                .apiName(apiName)
                .method(method)
                .endpoint(endpoint)
                .reqLog(reqLog)
                .createdAt(LocalDateTime.now())
                .build();
        return apiLogRepository.save(log);
    }

    public void updateResponse(String txId, Map<String, Object> resLog, int statusCode, int elapsedMs) {
        apiLogRepository.findByTxId(txId).ifPresent(log -> {
            log.setResLog(resLog);
            log.setStatusCode(statusCode);
            log.setElapsedMs(elapsedMs);
            log.setCompletedAt(LocalDateTime.now());
            apiLogRepository.save(log);
        });
    }

    public ApiLog getByTxId(String txId) {
        return apiLogRepository.findByTxId(txId).orElse(null);
    }

    public List<ApiLog> getRecentLogs() {
        return apiLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .toList();
    }
}