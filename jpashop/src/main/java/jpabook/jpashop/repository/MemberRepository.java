package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // 스프링을 사용하므로 사용 가능. 컴포넌트 스캔의 대상이 돼서 자동으로 스프링 빈으로 관리된다.
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext // 스프링 데이터 JPA를 사용하면 @Autowired로 변경할 수 있어서 생략 가능
    // 원래 EntityManager는 Autowired로는 안되고 PersistenceContext 라는 표준 애너테이션이 있어야 주입이 된다.
    // 다만, 스프링 데이터 JPA가 @Autowired로도 주입되도록 지원해주기 때문에 가능한 것이다.
    // JPA를 사용하기 때문에, JPA가 제공하는 표준 애너테이션인 @PersistenceContext 를 사용하고,
    private final EntityManager em;
    // EntityManager를 선언하면, 스프링이 엔티티 매니저를 만들어서 여기에 주입해준다.

    public void save(Member member) {
        em.persist(member);
        // 영속성 컨텍스트에 member 엔티티 객체를 넣는다.
        // 나중에 트랜젝션이 커밋 되는 시점에 DB에 insert 쿼리가 날아가면서 반영이 된다.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
        // JPA의 find()메소드를 사용. 첫번째 파라미터는 타입, 두번째 파라미터는 pk
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
        // 첫번째 파라미터에는 JPQL, 두번째 파라미터에는 반환 타입
        // JPQL은 SQL와 문법은 약간 다르지만, 기능적으로 거의 동일함. 결국 SQL로 번역되어야 하므로.
        // 차이는, SQL은 테이블을 대상으로 쿼리를 하는데, JPQL은 엔티티 객체를 대상으로 쿼리를 한다.
        // Member 엔티티 객체에 대한 alias를 m으로 주고, 조회해라.
        // getResultList()로 리스트로 만들어 준다.
    }

    public List<Member> findByName(String name) {
        // 이름으로 회원을 검색해야하면, 파라미터로 name을 넘기고 JPQL을 짜면 된다. 이때 where문이 들어감.
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
        // :name이 파라미터를 바인딩하는 것. 조회 타입은 Member.class
        // setParameter로 파라미터를 넣어주고, List로 반환한다.
    }

}
