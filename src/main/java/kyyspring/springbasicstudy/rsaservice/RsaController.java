package kyyspring.springbasicstudy.rsaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class RsaController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/api/rsaPublicKey")
    public Map<String, String> publicKey(){
        Map<String, String> keyPair = RsaUtil.createKeyPair();
        return Map.of("publicKey", keyPair.get("publicKey"));
    }

    @GetMapping("/api/session")
    public Map<String, String> getSecureKey() {
        Map<String, String> keyPair = RsaUtil.createKeyPair();

        // 고유 식별자(KeyID) 생성
        String keyId = UUID.randomUUID().toString();

        // Redis에 저장 (Key: keyId, Value: PrivateKey, 유효시간 3분)
        redisTemplate.opsForValue().set(
                "AUTH_KEY:" + keyId,
                keyPair.get("privateKey"),
                3,
                TimeUnit.MINUTES
        );

        // 클라이언트에게는 PublicKey와 KeyID를 줌
        return Map.of(
                "publicKey", keyPair.get("publicKey"),
                "keyId", keyId
        );
    }

    // 2. 로그인 요청
    @PostMapping("/api/login")
    public String login(@RequestBody LoginRequestDto request) {

        // 클라이언트가 보내준 keyId로 Redis 조회
        String redisKey = "AUTH_KEY:" + request.getKeyId();
        String privateKey = redisTemplate.opsForValue().get(redisKey).toString();

        if (privateKey == null) {
            throw new RuntimeException("유효하지 않은 키 ID 입니다.");
        }

        // 복호화
        String originalPassword = RsaUtil.decrypt(request.getEncryptedPassword(), privateKey);

        // 사용 후 Redis에서 즉시 삭제 (Replay Attack 방지 효과)
        redisTemplate.delete(redisKey);

        return "Process Login...";
    }

}
