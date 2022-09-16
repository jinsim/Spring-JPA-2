package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemQueryDto {

    @JsonIgnore // 엔티티가 아니라 api로 반환되는 것이므로, JsonIgnore를 사용해도 된다.
    private Long orderId; //주문번호
    private String itemName; //상품 명
    private int orderPrice; // 주문 가격
    private int count; //주문 수량

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
