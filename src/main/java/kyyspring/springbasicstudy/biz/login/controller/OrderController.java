package kyyspring.springbasicstudy.biz.login.controller;

import kyyspring.springbasicstudy.biz.login.domain.Order;
import kyyspring.springbasicstudy.biz.login.request.CreateOrderRequest;
import kyyspring.springbasicstudy.biz.login.request.UpdateOrderRequest;
import kyyspring.springbasicstudy.biz.login.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ 본인의 주문만 조회 가능
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('MEMBER') and @orderService.isOrderOwner(#orderId, authentication.name)")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId, Authentication auth) {
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(order);
    }

    // ✅ 회원이면서 활성 상태인 사용자만
    @PostMapping
    @PreAuthorize("hasRole('MEMBER') and @userService.isActiveUser(authentication.name)")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request, Authentication auth) {
        Order order = orderService.createOrder(request, auth.getName());
        return ResponseEntity.ok(order);
    }

    // ✅ 관리자이거나 본인 주문인 경우
    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MEMBER') and @orderService.isOrderOwner(#orderId, authentication.name))")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody UpdateOrderRequest request) {
        Order order = orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(order);
    }

    // ✅ 특정 조건을 만족하는 회원만
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('MEMBER') and @orderService.canCancelOrder(#orderId, authentication.name)")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId, "회원만 삭제 가능");
        return ResponseEntity.ok("주문이 취소되었습니다");
    }
}
