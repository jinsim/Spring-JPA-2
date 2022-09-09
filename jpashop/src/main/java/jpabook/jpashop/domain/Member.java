package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    // 연관 관계의 거울이므로 mappedBy를 넣고, orders Table에 있는 member 필드에 의해서 매핑되었다는 것을 알려준다.
    // orders는 매핑 하는 주체가 아니라, 매핑 받는 거울이라고 알려주는 것. 여기에 값을 넣는다고 foreign key가 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();
}
