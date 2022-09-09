package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
// 변경감지와 수정 관련 테스트
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX. 트랜젝션 안에서 이름 바꾸고,
        book.setName("asdfasdf");

        //TX commit. 트렌젝션이 커밋이 되면, JPA가 변경 부분을 찾아서 업데이트 쿼리를 만들어서 DB에 반영을 한다. = 더티체킹 = 변경감지

    }
}
