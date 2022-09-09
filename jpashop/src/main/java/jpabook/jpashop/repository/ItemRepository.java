package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    // 스프링 데이터 JPA가 대신 주입해준다.(persistenceContext 대신 autowired로 해줘서 생략 가능)
    private final EntityManager em;

    public void save(Item item) {
        // 아이템은 JPA에 저장하기 전까지는 id가 없다. 즉, 새로 생성한 객체이다.
        // 그래서 JPA가 제공하는 persist를 사용해서 신규로 등록한다.
        if (item.getId() == null) {
            em.persist(item);
        } else {
            // 그게 아니면 이미 DB에 등록된 객체를 가져온 것이다.
            // 그래서 merge를 하는데, 업데이트와 비슷한 것이다.
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        // 단건 조회는 find를 사용하면 되지만, 여러 개를 찾는 거는 JPQL 쿼리를 작성해야 한다.
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
