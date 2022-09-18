package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }


    /*      만들어야 하는 JPQL
        return em.createQuery("select o from Order o join o.member m" +
                "where o.status = :status" +
                "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    */

    /**
     * JPQL 문자로 작성
     */
    public List<Order> findAllByString(OrderSearch orderSearch) {

        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true; // sql에서 처음 조건은 where고 그 다음부터 and로 연결해야 하기 때문

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria 로 작성
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" +
                    orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();
    }
    /**
     * QueryDSL 로 작성
     */
    public List<Order> findAll(OrderSearch orderSearch) {
        // 생성자를 만들어서 this.query에 넣는 방법도 있다.
//        JPAQueryFactory query = new JPAQueryFactory(em);

        // QueryDSL을 만드려면, Q라는 파일을 미리 생성해야 한다. (단점)
        // Q파일을 통해서 변수 선언
        // static import로 줄여서 사용할 수도 있다.
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        // 아래의 쿼리가 JPQL로 변환돼서 실행된다. 코드가 직관적이다.
        // 100% 자바 코드이기 때문에, 오타를 컴파일 오류로 바로 볼 수 있다.
        return query
                .select(order)
                .from(order)
                // order에서 member를 join하고, alias를 위에 변수로 선언한 member로 둔다.
                .join(order.member, member)
                // 정적 쿼리. JPQL
//                .where(order.status.eq(orderSearch.getOrderStatus()), member.name.like(orderSearch.getMemberName()))
                // 동적 쿼리. QueryDSL
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    // OrderStatus가 같은지 비교하는 메서드
    private BooleanExpression statusEq(OrderStatus statusCond) {
        // 상태가 없으면 아무것도 하지 않는다.
        if (statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    // MemberName이 같은지 비교하는 메서드
    private BooleanExpression nameLike(String memberName) {
        if (StringUtils.hasText(memberName)) {
            return null;
        }
        return QMember.member.name.like(memberName);
    }

    public List<Order> findAllWithMemberDelivery() {
        // Order와 Member와 Delivery를 Join해서 select 절에다가 다 넣고, 한번에 다 땡겨오는 것
        // LAZY로 설정되어 있지만 무시하고, 프록시도 아니라 진짜 객체의 값을 다 채워서 가져온다.
        // = FETCH JOIN.
        // 기술적으로는 SQL에 JOIN을 사용하는데, FETCH라는 명령어는 SQL에 없다. JPA에만 있는 문법이다.
        // 패치 조인의 경우, JPA 기본편 강의를 참고하여 100% 이해하는 것이 좋다.
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        // 실무에서는 이렇게 조금 복잡한 쿼리를 짤 때는, QueryDSL을 사용한다.
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
