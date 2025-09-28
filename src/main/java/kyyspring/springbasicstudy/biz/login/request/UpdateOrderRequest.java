package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 주문 수정 요청
public class UpdateOrderRequest {

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Max(value = 999, message = "수량은 999개를 초과할 수 없습니다")
    private Integer quantity;

    @Size(max = 200, message = "배송 주소는 200자를 초과할 수 없습니다")
    private String shippingAddress;

    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    private String shippingPhone;

    @Size(max = 500, message = "주문 메모는 500자를 초과할 수 없습니다")
    private String orderNotes;

    // getter, setter
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }

}