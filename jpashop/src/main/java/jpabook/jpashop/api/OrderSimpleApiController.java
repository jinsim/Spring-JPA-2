package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Order, Order -> Member(ManyToOne), Order -> Delivery(OneToOne) 조회
 * xToOne 관계에서의 조회 성능 최적화를 다룰 것이다.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

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
}
