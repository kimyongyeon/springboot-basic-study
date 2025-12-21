package kyyspring.springbasicstudy.biz.file.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // 테스트를 위한 CORS 허용
@RequestMapping("/api")
public class JsonFileController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public Map<String, Object> uploadJson(@RequestParam("jsonFile") MultipartFile file) {
        try {
            // 1. 파일 데이터 읽기 (InputStream 사용)
            // Jackson의 readValue는 스트림을 직접 읽어 Map으로 변환합니다.
            Map<String, Object> map = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // 2. 가공된 데이터 확인
            System.out.println("수신 데이터: " + map);

            // 3. 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("receivedData", map);
            return response;

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 실패: " + e.getMessage());
        }
    }

    // DTO를 대신할 클래스 선언 (또는 Map 사용 가능)
    @Data
    public static class UserDto {
        public String username;
        public String age;
        public String description;
        public String name;
    }

    @PostMapping("/upload/mixed")
    public Map<String, Object> handleMixedUpload(
            @RequestPart("user") UserDto userDto,       // JSON 파트를 DTO로 자동 변환
            @RequestPart("file") MultipartFile file      // 파일 파트 수신
    ) {
        // 1. 파일 정보 확인
        System.out.println("파일명: " + file.getOriginalFilename());
        System.out.println("파일 크기: " + file.getSize());

        // 2. DTO 데이터 확인
        System.out.println("사용자 이름: " + userDto.username);
        System.out.println("설명: " + userDto.description);

        // 3. 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "데이터와 파일 수신 성공");
        response.put("receivedUser", userDto.username);
        return response;
    }

    // 1. DTO 구조 정의
    @Data
    public class UserRegistrationDto {
        private String username;
        private String description;
        private MultipartFile file; // 파일 필드를 DTO 안에 포함

        // Getter, Setter 필수
    }

    // 2. 컨트롤러 (간결해짐)
    @PostMapping("/upload/simple")
    public Map<String, Object> handleSimpleUpload(@ModelAttribute UserRegistrationDto dto) {
        System.out.println("유저명: " + dto.getUsername());
        System.out.println("파일명: " + dto.getFile().getOriginalFilename());

        return Map.of("status", "success");
    }

    @Getter
    @Setter
    @ToString
    public class OrderDto {
        private String dataType; // "ORDER"
        private String orderId;
        private long amount;
        private List<String> items; // 주문 항목 리스트
        private String orderDate;
    }

    @PostMapping("/single-file")
    public String handleSingleFile(@RequestPart("file") MultipartFile file) {
        try {
            // 1. 파일이 비어있는지 확인
            if (file.isEmpty()) return "파일이 없습니다.";

            // 2. 파일 내용을 JsonNode로 먼저 읽기 (메모리 효율적)
            // JsonNode를 쓰면 전체를 객체로 바꾸기 전에 특정 키값만 미리 볼 수 있습니다.
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());

            // 3. 'dataType' 필드 추출
            String dataType = rootNode.path("dataType").asText();

            // 4. 타입에 따른 분기 처리 및 DTO 변환
            if ("USER".equals(dataType)) {
                // JsonNode를 구체적인 클래스로 변환 (treeToValue 활용)
                // 변경 후 (data 필드 진입)
                UserDto data = objectMapper.treeToValue(rootNode.path("data"), UserDto.class);
//                UserDto userDto = objectMapper.treeToValue(rootNode, UserDto.class);
                return "User 처리: " + data.getName();

            } else if ("ORDER".equals(dataType)) {
                OrderDto orderDto = objectMapper.treeToValue(rootNode, OrderDto.class);
                return "Order 처리: " + orderDto.getOrderId();
            }

            return "알 수 없는 데이터 타입입니다.";

        } catch (IOException e) {
            return "파일 처리 중 오류 발생: " + e.getMessage();
        }
    }
}