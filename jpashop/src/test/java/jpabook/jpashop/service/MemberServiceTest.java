package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

// 스프링과 통합해서, 스프링 부트를 올려서 실행하려면 밑의 두 애너테이션이 필요하다.
@RunWith(SpringRunner.class)
@SpringBootTest
// 데이터를 변경해야하므로. 이게 있어야 롤백이 된다.
@Transactional
public class MemberServiceTest {

    // 테스트 케이스므로 다른 것들이 참조하지 않는다. 그러므로 그냥 간단하게 해주면 된다.
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Autowired EntityManager em; // 롤백은 되지만, flush 해서 DB에 반영시킬 목적으로 추가.

    @Test
//    @Rollback(false) // @Transactional이 테스트케이스에 있으면 커밋을 하는 게 아니라 롤백을 한다. 따라서 DB에 인서트문이 가지 않는다.
    // 등록 쿼리까지 보고 싶으면 이 애너테이션을 붙여주자
    public void 회원가입() throws Exception {
        // 테스트는 보통 이런 게 주어졌을 때, 이렇게 하면, 이렇게 된다. = given when then
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); // 영속성 컨텍스트에 있는 등록이나 변경 내용을 DB에 반영한다.
        // 테스트가 종료되면 @Transactional의 영향으로 롤백된다
        assertEquals(member, memberRepository.findOne(savedId));
        // JPA에서 같은 트랜젝션 안에서 PK 값이 똑같으면, 같은 영속성 컨텍스트에서 하나로 관리된다. 따라서 True
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);

//        try {
//            memberService.join(member2); // 예외가 발생해야 한다!
//        } catch (IllegalStateException e) {
//            return;
//        }

        //then
        fail("에외가 발생해야 한다.");
    }
}