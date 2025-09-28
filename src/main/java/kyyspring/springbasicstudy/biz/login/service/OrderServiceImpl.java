package kyyspring.springbasicstudy.biz.login.service;

import jakarta.persistence.EntityNotFoundException;
import kyyspring.springbasicstudy.biz.login.domain.Order;
import kyyspring.springbasicstudy.biz.login.domain.OrderStatus;
import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.repository.OrderRepository;
import kyyspring.springbasicstudy.biz.login.request.CreateOrderRequest;
import kyyspring.springbasicstudy.biz.login.request.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final UserService userService;

    @Override
    public Order createOrder(CreateOrderRequest request, String userEmail) {
        try {
            User user = userService.findByEmail(userEmail);

            Order order = new Order(
                    user.getId(),
                    request.getProductId(),
                    request.getProductName(),
                    request.getQuantity(),
                    request.getUnitPrice()
            );

            order.setShippingAddress(request.getShippingAddress());
            order.setShippingPhone(request.getShippingPhone());
            order.setOrderNotes(request.getOrderNotes());
            order.setPaymentMethod(request.getPaymentMethod());

            Order savedOrder = orderRepository.save(order);
            logger.info("Order created: orderId={}, userId={}", savedOrder.getId(), user.getId());

            return savedOrder;
        } catch (Exception e) {
            logger.error("Failed to create order for user: {}", userEmail, e);
            throw new RuntimeException("주문 생성에 실패했습니다", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Order updateOrder(Long orderId, UpdateOrderRequest request) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("대기 중인 주문만 수정할 수 있습니다");
        }

        if (request.getQuantity() != null) {
            order.setQuantity(request.getQuantity());
            order.setTotalPrice(order.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        }

        if (request.getShippingAddress() != null) {
            order.setShippingAddress(request.getShippingAddress());
        }

        if (request.getShippingPhone() != null) {
            order.setShippingPhone(request.getShippingPhone());
        }

        if (request.getOrderNotes() != null) {
            order.setOrderNotes(request.getOrderNotes());
        }

        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order updated: orderId={}", orderId);

        return updatedOrder;
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("대기 중인 주문만 삭제할 수 있습니다");
        }

        orderRepository.delete(order);
        logger.info("Order deleted: orderId={}", orderId);
    }

    @Override
    public Order confirmOrder(Long orderId) {
        Order order = findById(orderId);
        order.confirm();

        Order confirmedOrder = orderRepository.save(order);
        logger.info("Order confirmed: orderId={}", orderId);

        return confirmedOrder;
    }

    @Override
    public Order shipOrder(Long orderId, String trackingNumber) {
        Order order = findById(orderId);
        order.ship(trackingNumber);

        Order shippedOrder = orderRepository.save(order);
        logger.info("Order shipped: orderId={}, trackingNumber={}", orderId, trackingNumber);

        return shippedOrder;
    }

    @Override
    public Order deliverOrder(Long orderId) {
        Order order = findById(orderId);
        order.deliver();

        Order deliveredOrder = orderRepository.save(order);
        logger.info("Order delivered: orderId={}", orderId);

        return deliveredOrder;
    }

    @Override
    public Order cancelOrder(Long orderId, String reason) {
        Order order = findById(orderId);
        order.cancel(reason);

        Order cancelledOrder = orderRepository.save(order);
        logger.info("Order cancelled: orderId={}, reason={}", orderId, reason);

        return cancelledOrder;
    }

    // ✅ @PreAuthorize에서 사용할 보안 메서드들
    @Override
    @Transactional(readOnly = true)
    public boolean isOrderOwner(Long orderId, String userEmail) {
        try {
            Order order = findById(orderId);
            User user = userService.findByEmail(userEmail);
            return order.isOwnedBy(user.getId());
        } catch (Exception e) {
            logger.error("Order ownership check failed: orderId={}, email={}", orderId, userEmail, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelOrder(Long orderId, String userEmail) {
        try {
            Order order = findById(orderId);
            User user = userService.findByEmail(userEmail);

            return order.isOwnedBy(user.getId()) && order.canBeCancelled();
        } catch (Exception e) {
            logger.error("Order cancellation check failed: orderId={}, email={}", orderId, userEmail, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findUserOrdersByStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findUserOrdersPaged(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getOrderCountByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOrderAmountByUserId(Long userId) {
        return orderRepository.getTotalOrderAmountByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<OrderStatus, Long> getOrderStatistics(LocalDateTime since) {
        List<Object[]> results = orderRepository.getOrderStatisticsSince(since);

        Map<OrderStatus, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            OrderStatus status = (OrderStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status, count);
        }

        return statistics;
    }
}
