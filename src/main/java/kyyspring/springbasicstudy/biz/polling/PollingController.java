package kyyspring.springbasicstudy.biz.polling;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import kyyspring.springbasicstudy.sys.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayOutputStream;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PollingController {

    private final PollingService pollingService;
    private final RedisService redisService;

    @GetMapping("/polling")
    public PollingResponse polling() {
        log.info("polling");
        PollingEntity pollingEntity = new PollingEntity();
        pollingEntity.setId(1L);
        pollingEntity.setName("kyy");
        pollingEntity.setPhone("010-1234-5678");
        pollingService.savePolling(pollingEntity);
        PollingEntity polling = pollingService.getPolling(1L);
        log.info("polling: {}", polling);

        return PollingResponse.builder()
                .id(polling.getId())
                .name(polling.getName())
                .phone(polling.getPhone())
                .build();
    }

    /**
            * QR 코드 생성을 위한 세션 UUID를 요청
     * @return UUID와 QR 코드 URL
     */
    @GetMapping("/generate-qr")
    public ResponseEntity<Map<String, String>> generateQr() {
        String uuid = redisService.createQrSession();
        // 실제로는 QR 코드 이미지 생성 라이브러리를 사용하거나, 프론트에서 UUID를 기반으로 QR 코드를 생성해야 함
        String qrUrl = "http://your-app.com/qr-auth?uuid=" + uuid;

        Map<String, String> response = new HashMap<>();
        response.put("uuid", uuid);
        response.put("qrUrl", qrUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * 프론트엔드에서 UUID의 상태를 확인
     * @param uuid 확인할 세션 UUID
     * @return 현재 상태
     */
    @GetMapping("/status/{uuid}")
    public ResponseEntity<Map<String, String>> checkStatus(@PathVariable String uuid) {
        String status = redisService.getStatus(uuid);
        Map<String, String> response = new HashMap<>();
        response.put("status", status != null ? status : "expired"); // 상태가 없으면 만료
        return ResponseEntity.ok(response);
    }

    /**
     * 모바일 앱에서 인증 완료 후 호출
     * @param uuid 인증 완료된 세션 UUID
     * @return 성공 메시지
     */
    @PostMapping("/verify/{uuid}")
    public ResponseEntity<String> verifyQr(@PathVariable String uuid) {
        redisService.updateStatus(uuid, "success");
        return ResponseEntity.ok("Verified successfully");
    }

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String content) throws WriterException, IOException {

        // 1. QR 코드 설정
        int width = 200;
        int height = 200;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 오류 복원 레벨 (L, M, Q, H)

        // 2. QR 코드 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String format = String.format("http://localhost:8080/verify/%s", content);

        BitMatrix bitMatrix = qrCodeWriter.encode(format, BarcodeFormat.QR_CODE, width, height, hints);

        // 3. BitMatrix를 이미지 바이트 배열로 변환
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        // 4. HTTP 응답으로 반환
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(pngOutputStream.toByteArray());
    }
}
