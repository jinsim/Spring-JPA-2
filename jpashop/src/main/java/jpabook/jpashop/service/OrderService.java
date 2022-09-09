package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional // DB를 변경하므로
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성.
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성. 예제 단순화를 위해 하나만 넘긴다.
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        // 현재 Order와 delivery, orderItem의 CascadeType이 ALL로 설정되어 있다.
        // 따라서 order만 persist 해주면 delivery와 orderItem도 persist가 강제로 된다.

        return order.getId();
    }

    /**
     * 취소
     */
    @Transactional // DB에 변화가 있다.
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
        // 원래는 내부 코드에서 일어난 변화들도 다 직접 업데이트 쿼리를 서비스 계층에서 작성 해야했다.
        // JPA를 사용하면 더티체킹 과정을 거쳐 엔티티 내부 변화를 감지하여 이를 DB에 반영해준다.
    }

    /**
     * 검색
     * 단순하게 위임해서 조회하는 기능이면, 컨트롤러에서 리포지토리를 바로 불러도 괜찮다.
     * 아키텍쳐를 어떻게 다루는지에 따라서 다르다.
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
