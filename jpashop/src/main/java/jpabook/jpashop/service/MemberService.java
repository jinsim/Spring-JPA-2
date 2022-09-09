package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 안에 @component가 있다.
@Transactional(readOnly = true)
// JPA의 모든 데이터 변경이나 로직들은 트랜젝션 안에서 실행되어야 한다. 그래야 Lazy 로딩 등이 된다.
// 클래스 레벨에 @Transactional을 쓰면, public 메소드들에 기본적으로 @Transactional이 걸려서 들어간다.
// Transactional에는 javax가 있고, 스프링이 제공해주는 게 있다.
// 이미 스프링을 쓰고, 스프링에 디펜던시한 로직이 많이 들어갔으므로 스프링을 사용하는 것이 좋다. 쓸 수 있는 옵션들이 더 많다.
// @Transactional에 readOnly 옵션을 true로 두면, JPA가 조회하는 곳에서는 성능을 최적화한다.
// 쓰기에서는 넣으면 안된다. 데이터 변경이 안된다.
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    @Transactional // 디폴트는 readOnly가 False임.
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        // JPA에서 em.persist를 하면, 영속성 컨텍스트에 멤버 객체를 올린다.
        // 그때 영속성 컨텍스트에서는 id값이 key가 된다. (DB pk랑 매핑한 게 key가 됨)
        // @GeneratedValue를 세팅하면 id값이 항상 들어가있는 것이 보장이 된다. (em.persist 할 때)
        // 왜냐하면, 영속성 컨텍스트에 값을 넣어야 하는데, key value 구조를 채우기 위해서.
        // 따라서 아직 DB에 들어가지 않았는데도 id값이 생성되어 있다.
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // id로 회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
