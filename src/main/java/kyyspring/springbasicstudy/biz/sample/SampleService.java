package kyyspring.springbasicstudy.biz.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import kyyspring.springbasicstudy.biz.comm.response.UserReponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SampleService {

    public void init() {
        ObjectMapper mapper = new ObjectMapper();

        UserReponse dto = new UserReponse();
        dto.setAge(123);
        dto.setName("test user man");

        // DTO → Map 변환
        Map<String, Object> map = mapper.convertValue(dto, Map.class);
        log.info("map: {}", map.toString());

        // Map → DTO 역변환도 가능
        UserReponse restored = mapper.convertValue(map, UserReponse.class);
        log.info("restored: {}", restored);

        Map<String, Object> map2 = Map.of(
                "userId", "kim123",
                "profile", Map.of("name", "Kim", "age", 33),
                "devices", List.of(
                        Map.of("os", "Android", "version", "14"),
                        Map.of("os", "iOS", "version", "17")
                )
        );
        UserReponse restored2 = mapper.convertValue(map2, UserReponse.class);
        log.info("restored2: {}", restored2);
    }

}
