package kyyspring.springbasicstudy.biz.login.service;

import kyyspring.springbasicstudy.biz.login.domain.Order;
import kyyspring.springbasicstudy.biz.login.domain.OrderStatus;
import kyyspring.springbasicstudy.biz.login.request.CreateOrderRequest;
import kyyspring.springbasicstudy.biz.login.request.UpdateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // 기본 CRUD
    Order createOrder(CreateOrderRequest request, String userEmail);
    Order findById(Long orderId);
    List<Order> findByUserId(Long userId);
    Order updateOrder(Long orderId, UpdateOrderRequest request);
    void deleteOrder(Long orderId);

    // 주문 상태 관리
    Order confirmOrder(Long orderId);
    Order shipOrder(Long orderId, String trackingNumber);
    Order deliverOrder(Long orderId);
    Order cancelOrder(Long orderId, String reason);

    // 보안 관련
    boolean isOrderOwner(Long orderId, String userEmail);
    boolean canCancelOrder(Long orderId, String userEmail);

    // 조회 관련
    List<Order> findByStatus(OrderStatus status);
    List<Order> findUserOrdersByStatus(Long userId, OrderStatus status);
    Page<Order> findUserOrdersPaged(Long userId, Pageable pageable);

    // 통계 관련
    Integer getOrderCountByUserId(Long userId);
    BigDecimal getTotalOrderAmountByUserId(Long userId);
    Map<OrderStatus, Long> getOrderStatistics(LocalDateTime since);
}
