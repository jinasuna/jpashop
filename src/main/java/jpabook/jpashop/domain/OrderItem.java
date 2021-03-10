package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 품번 1234의 가방 item은 한개, 주문될 수 있는 order_item은 여러개 가능(품번 1234가 이 주문에 여러개이거나 여러 주문에 나뉘어 들어간다던지..?)
    @JoinColumn(name = "item_id")
    private Item item;

    // 일대 다 중 "다" 역할
    @ManyToOne(fetch = FetchType.LAZY) // OrderItem의 order_id쪽이 foreign키이기 때문에 OrderItem의 order_id가 연관관계 주인이 된다.
    @JoinColumn(name = "order_id") // order_id라는 이름의 컬럼은 Order클래스 객체를 만들어 가져와라
    private Order order;

    private int orderPrice; // 주문 가격 (주문 당시의 가격)
    private int count;      // 주문 수량

    // jpa는 protected까지 constructor를 만들도록 허락해 준다. -> lombok으로 줄일 수 있다.
    // 비즈니스 로직에서 new로 막 만들어서 set하지 않도록 막아줘야 한다.
    // jpa 쓰면서 protected 설정 ! 쓰지 말라는 것
    // protected OrderItem(){}

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // OrderItem을 생성할 때에는 기본적으로 재고를 까줘야 한다.
        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() { // 재고를 다시 주문수량 만큼 늘려줘야 한다.
        getItem().addStock(count); // 재고수량을 원상복구
    }

    //==조회 로직==//
    /**
     * 주문상품 전체 가격 조회
     */
    // 주문할 때 주문 가격과 수량이 있기 때문에 두 개를 곱해야 한다.
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
