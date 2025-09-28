package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

// 주문 생성 요청
@Valid
@Data
public class CreateOrderRequest {

    @NotNull(message = "상품 ID는 필수입니다")
    private Long productId;

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다")
    private String productName;

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Max(value = 999, message = "수량은 999개를 초과할 수 없습니다")
    private Integer quantity;

    @NotNull(message = "단가는 필수입니다")
    @DecimalMin(value = "0.01", message = "단가는 0.01 이상이어야 합니다")
    private BigDecimal unitPrice;

    @NotBlank(message = "배송 주소는 필수입니다")
    @Size(max = 200, message = "배송 주소는 200자를 초과할 수 없습니다")
    private String shippingAddress;

    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    private String shippingPhone;

    @Size(max = 500, message = "주문 메모는 500자를 초과할 수 없습니다")
    private String orderNotes;

    @NotBlank(message = "결제 방법은 필수입니다")
    private String paymentMethod;

    // 생성자, getter, setter
    public CreateOrderRequest() {}
}

