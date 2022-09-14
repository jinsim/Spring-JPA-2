package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        // select가 o로 되면 DTO에 매핑될 수가 없다.
        // JPA는 엔티티나 Value Object(Address 등)만 반환할 수 있다.
        // DTO를 반환하려면, new Operation을 꼭 써야한다.

        return em.createQuery(
//                "select o from Order o" +
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(" +
                                "o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();

    }
}
