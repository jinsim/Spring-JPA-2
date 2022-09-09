package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") // 테이블명 + _id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계
    @JoinColumn(name = "member_id") // 매핑을 뭐로 할거냐. foreign key의 이름이 member_id가 된다.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    // orderItems에 데이터를 넣어두고 order를 저장하면, orderItems도 같이 저장된다.
    // 원래는 JPA의 persist로 아이템들을 넣고, 그 다음 order에 persist를 해야 한다.
    //  persist(orederItemA)
    //  persist(orederItemB)
    //  persist(orederItemC)
    //  persist(order)
    // cascade를 하면 order에만 persist를 해도 된다. cascade가 persist를 전파하기 때문
    //  persist(order)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // order를 저장할 때, delivery 객체만 세팅해두면, delivery도 같이 persist 해준다.
    // 원래는 delivery도 persist, order도 persist 각각 해줘야 한다.
    // 모든 엔티티는 기본적으로 persist를 각각 해줘야 한다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // private Date data를 사용하면 날짜 관련된 애너테이션 매핑을 해야한다.
    // 자바 8부터는 LocalDateTime를 사용하면 하이버네이트가 알아서 지원해준다.
    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    //==연관관계 메서드==//

    // setMember를 하면 양방향으로 각각 데이터를 넣어준다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            // 여기서 orderItems를 this.orderItems이라고 써도 된다.
            // 영한님의 경우, 강조할 때나 이름이 똑같을 때 외는 this 사용하지 않는다. 그냥 스타일임.
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        // 현재 나한텐 정보가 없고, orderItem들을 전부 더하면 된다.
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
        // 스트림으로 변경한 코드
//        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
