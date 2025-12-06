package kyyspring.springbasicstudy.countservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginFailService {

    private final StringRedisTemplate redisTemplate;

    // 최대 실패 횟수
    private static final int MAX_FAIL_COUNT = 5;

    // 24시간 기준 락을 쓸 거면:
    private static final long LOCK_SECONDS_24H = 24 * 60 * 60;

    private String buildFailKey(String userId) {
        return "login:fail:" + userId;
    }

    /**
     * 현재 잠금 상태인지 확인
     */
    public boolean isLocked(String userId) {
        String key = buildFailKey(userId);
        String val = redisTemplate.opsForValue().get(key);

        if (val == null) {
            return false;
        }
        try {
            int count = Integer.parseInt(val);
            return count >= MAX_FAIL_COUNT;
        } catch (NumberFormatException e) {
            // 이상한 값 들어가면 안전하게 잠금 해도 되고, 초기화해도 됨
            return true;
        }
    }

    /**
     * 로그인 실패 시 호출
     * @return 현재 실패 횟수
     */
    public int increaseFailCount(String userId) {
        String key = buildFailKey(userId);

        // 실패 횟수 증가
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            count = 1L;
        }

        // 첫 실패라면 TTL 설정
        if (count == 1L) {
            // 1) 단순 24시간 기준
            redisTemplate.expire(key, LOCK_SECONDS_24H, TimeUnit.SECONDS);

            // 2) 만약 "자정까지"로 하고 싶으면 아래처럼 계산해서 사용
            // long secondsUntilMidnight = getSecondsUntilMidnight();
            // redisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
        }

        return count.intValue();
    }

    /**
     * 수동 리셋 (관리자가 잠금 해제할 때 등)
     */
    public void resetFailCount(String userId) {
        String key = buildFailKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 오늘 자정까지 남은 초 (00:00 기준으로 리셋하고 싶을 때 사용)
     */
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration duration = Duration.between(now, midnight);
        return duration.getSeconds();
    }
}