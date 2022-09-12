package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order, Order -> Member(ManyToOne), Order -> Delivery(OneToOne) 조회
 * xToOne 관계에서의 조회 성능 최적화를 다룰 것이다.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // getMember까지 Member는 프록시 객체.
            // getName을 하면 실제 Name을 DB에서 가져와야 하므로, LAZY 강제 초기화가 된다.
            // hibernate가 Member의 데이터를 직접 가져와서 채워준다.
            order.getDelivery().getAddress(); // 아무 필드나 가져와서 LAZY 강제 초기화를 시켜준다.
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {

        // 조회 쿼리 1개 발생. 결과인 Order 수는 2개이다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // 루프를 돌면서 객체 2개 모두 다 new SimpleOrderDto()가 실행되면서, 쿼리가 2개씩 추가로 더 발생한다.
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // DTO에서 Entity를 파라미터로 받는 것은 괜찮다.
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
