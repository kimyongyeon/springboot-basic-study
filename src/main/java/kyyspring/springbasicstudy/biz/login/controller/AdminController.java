package kyyspring.springbasicstudy.biz.login.controller;

import kyyspring.springbasicstudy.biz.login.request.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // ✅ 관리자만 접근
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok("사용자 목록");
    }

    // ✅ 특정 권한을 가진 사용자만
    @PostMapping("/products")
    @PreAuthorize("@securityExpressions.hasPermission(authentication.name, 'CREATE_PRODUCT')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok("상품 생성");
    }

    // ✅ 리소스별 접근 제어
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.canAccessResource(authentication.name, 'user', #userId)")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok("사용자 정보: " + userId);
    }

    // ✅ 시간 기반 접근 제어
    @PostMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN') and @securityExpressions.isBusinessHours()")
    public ResponseEntity<?> performMaintenance() {
        return ResponseEntity.ok("점검 실행");
    }

    // ✅ 복합 조건
    @DeleteMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN') and @orderService.canCancelOrder(#orderId, authentication.name) and @securityExpressions.isBusinessHours()")
    public ResponseEntity<?> forceCancel(@PathVariable Long orderId) {
        return ResponseEntity.ok("강제 취소");
    }
}
