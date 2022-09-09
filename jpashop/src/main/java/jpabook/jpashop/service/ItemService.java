package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    // readOnly = true면 DB에 반영이 되지 않기 때문에 오버라이딩 해서 해당 메소드는 false로 맞춰준다.
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경 감지 기능을 사용해서 준영속 엔티티 수정
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        // id를 기반으로 실제 DB에 있는 영속 상태 엔티티를 찾아왔따.
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        // 이런 식으로 필드를 채우고, itemRepository.save나, em의 persist나 merge를 실행할 필요가 없다.
        // 리포지토리에서 찾아온 findItem은 영속 상태 엔티티임. param으로 넘어온 데이터로 값만 세팅하고 updateItem 메서드가 끝나면,
        // 스프링의 @Transactional에 의해서 트렌젝션이 커밋이 된다. 커밋이 되면 JPA는 flush를 날린다.
        // flush를 날리는 것은, 영속성 컨텍스트에 있는 엔티티 중에 변경된 것을 다 찾는다.
        // 바뀐 값을 DB에 업데이트 쿼리를 날려서 반영을 시킨다. == 변경 감지에 의해 데이터를 변경하는 방법이다.
        // 이게 보통은 더 나은 방법이다.
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
