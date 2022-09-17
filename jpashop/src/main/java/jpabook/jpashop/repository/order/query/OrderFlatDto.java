package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {
    // DB에서 한방에 다 가져올 것임. Order과 OrderItem 조인, OrderItem과 Item도 조인.
    // 한방 쿼리로 만든 다음에 데이터를 한 줄로 flat하게, SQL join의 결과를 그대로 가져올 수 있도록 데이터 구조를 맞춘다.
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; // 주문시간
    private Address address;
    private OrderStatus orderStatus;
    private String itemName; // 상품 명
    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus,
                        Address address, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
