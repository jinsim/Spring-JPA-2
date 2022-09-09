package jpabook.jpashop.domain.Item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 상속 관계 매핑이므로, 상속 관계 전략을 부모에 적어준다.
@DiscriminatorColumn(name = "dtype") // 싱글 테이블이므로, 각각의 객체가 저장될 때 구분을 위함
@Getter @Setter
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 상품 도메인 관련 비즈니스 로직==//

    /**
     * stockQuantity를 갖고 있는 곳에서 관련된 비즈니스 로직이 나가는 게 가장 응집도가 있을 것. (DDD, 객체지향)
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
