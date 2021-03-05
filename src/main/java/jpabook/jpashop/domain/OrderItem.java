package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne // 품번 1234의 가방 item은 한개, 주문될 수 있는 order_item은 여러개 가능(품번 1234가 이 주문에 여러개이거나 여러 주문에 나뉘어 들어간다던지..?)
    @JoinColumn(name = "item_id")
    private Item item;

    // 일대 다 중 "다" 역할
    @ManyToOne // OrderItem의 order_id쪽이 foreign키이기 때문에 OrderItem의 order_id가 연관관계 주인이 된다.
    @JoinColumn(name = "order_id") // order_id라는 이름의 컬럼은 Order클래스 객체를 만들어 가져와라
    private Order order;

    private int orderPrice; // 주문 가격 (주문 당시의 가격)
    private int count;      // 주문 수량

}
