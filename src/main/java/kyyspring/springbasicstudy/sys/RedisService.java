package kyyspring.springbasicstudy.sys;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * QR 코드 상태를 Redis에 저장하고 UUID를 반환합니다.
     * @return 생성된 UUID
     */
    public String createQrSession() {
        String uuid = UUID.randomUUID().toString();
        // 초기 상태: 'pending', 3분(180초) TTL 설정
        redisTemplate.opsForValue().set(uuid, "pending", Duration.ofSeconds(180));
        return uuid;
    }

    /**
     * 특정 UUID의 상태를 업데이트합니다. (모바일 앱이 인증 완료 시 호출)
     * @param uuid 상태를 업데이트할 UUID
     * @param status 변경할 상태 (예: "success", "failure")
     */
    public void updateStatus(String uuid, String status) {
        redisTemplate.opsForValue().set(uuid, status, Duration.ofSeconds(180));
    }

    /**
     * 특정 UUID의 현재 상태를 가져옵니다. (프론트엔드 폴링 시 호출)
     * @param uuid 상태를 확인할 UUID
     * @return 현재 상태
     */
    public String getStatus(String uuid) {
        return redisTemplate.opsForValue().get(uuid);
    }
}
