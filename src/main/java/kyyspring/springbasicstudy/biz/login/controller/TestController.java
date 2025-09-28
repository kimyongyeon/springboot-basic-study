package kyyspring.springbasicstudy.biz.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 회원만 접근 가능
    @GetMapping("/member/profile")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> getMemberProfile(Authentication auth) {
        return ResponseEntity.ok("회원 프로필: " + auth.getName());
    }

    // 비회원만 접근 가능
    @GetMapping("/guest/cart")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> getGuestCart(Authentication auth) {
        return ResponseEntity.ok("비회원 장바구니: " + auth.getName());
    }

    // 둘 다 접근 가능
    @GetMapping("/common/products")
    @PreAuthorize("hasRole('MEMBER') or hasRole('GUEST')")
    public ResponseEntity<?> getProducts(Authentication auth) {
        return ResponseEntity.ok("상품 목록");
    }
}