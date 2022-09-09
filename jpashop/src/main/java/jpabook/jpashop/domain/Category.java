package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    // 중간 테이블 매핑을 해줘야한다. 객체는 컬렉션을 사용해서 다대다 관계가 가능한데, RDB는 일대다 다대일 관계로 풀어야 가능하다.
    // 이 그림 밖에 안됨. 실무에서 사용할 수 없다. 단순하게 매핑만 하고 끝나는 경우가 없기 때문에.
    @JoinTable(name = "categoty_item",
            // 중간 테이블에 있는 category_id
            joinColumns = @JoinColumn(name = "category_id"),
            // categpry_item 테이블에 item 쪽으로 들어가는 item_id을 매핑해준다.
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
