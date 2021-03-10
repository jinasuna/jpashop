package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

// 일종의 통합 테스트
// 현재 만드는 테스트가 좋은 테스트라고 볼 수는 없다
// -> springboot와 integration해서 jpa가 동작하는 것을 자세히 보기 위한 테스트
// 좋은 테스트 : DBMS나 spring과 같은 환경 설정에 의존하지 않고 단위 테스트를 하는 것

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    // 테스트이기 때문에 단순하게 persist 하면 좋다.
    // EntityManager를 바로 받아오겠다. 테스트 데이터를 넣는게 목적이어서
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    // 상품 주문 테스트
    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("do it JPA", 15000, 20);

        //when
        int orderCount = 3;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then : 검증하는 로직
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus()); // 의도한 대로 order의 상태를 잘 가지고 있나?
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 15000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다", 17, book.getStockQuantity());

    }

    // 주문 취소 테스트
    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("JPA programming", 20000, 5);
        int orderCount = 1;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when : 실제 테스트 하고싶은 부분
        orderService.cancelOrder(orderId);

        //then : 재고가 정상적으로 원상복구 되었는지 검증
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 다시 증가해야 한다.", 5, book.getStockQuantity());
    }

    // 상품 주문과 관련하여 재고 수량 초과에 대해 제대로 검증을 해주는지 테스트
    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("do it JPA", 15000, 20);

        // 이럴 경우 exception 터지게 해야
        int orderCount = 21;
        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "구로", "123-456"));
        em.persist(member);
        return member;
    }

}