package kyyspring.springbasicstudy.biz.login.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "api_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_id", nullable = false, unique = true, length = 64)
    private String txId;

    @Column(name = "api_name", nullable = false, length = 200)
    private String apiName;

    @Column(name = "method", length = 10)
    private String method;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    /** JSONB 요청 데이터 */
    @Type(JsonType.class)
    @Column(name = "req_log", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> reqLog;

    /** JSONB 응답 데이터 */
    @Type(JsonType.class)
    @Column(name = "res_log", columnDefinition = "jsonb")
    private Map<String, Object> resLog;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "elapsed_ms")
    private Integer elapsedMs;

    @Column(name = "result_code", length = 20)
    private String resultCode;

    @Column(name = "result_msg", length = 500)
    private String resultMsg;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    @Column(name = "created_at", columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
