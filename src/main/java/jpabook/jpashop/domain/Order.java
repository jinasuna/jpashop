package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 양방향 연관관계 -> 연관관계 주인을 정해줘야 함
    // JPA는 뭘 믿고 foreign키 값을 누가 업데이트를 쳐야하지? -> 둘중에 하나만 선택을 하도록 JPA에서 약속했다.
    // 둘중 하나를 주인이라는 개념으로 잡아줘야(얘의 값이 변경되었을때 DB의 외래키 값을 바꿀거야 라고 지정)
    // 연관관계 주인 정하는 방법 : foreign키가 가까운 곳으로 할 것 -> orders에 외래키가 있으니 orders를 연관관계 주인으로 매핑
    @ManyToOne(fetch = LAZY)  // order랑 member는 다대 일 관계
    @JoinColumn(name = "member_id") // foreign키 이름이 member_id가 되는 것
    private Member member; // 주문한 회원에 관한 정보
    // 여기에 값을 매핑하면 member_id라는 foreign키 값이 다른 멤버로 변경된다.

    // order_id라는 이름의 컬럼을 OrderItem에서 Order클래스 객체 order를 만들어 가져왔기 때문에
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 이걸 넣어줘야 orderItems까지 전파가 된다.
    // OrderItem클래스가 order라는 이름으로 매핑되어 생김..? -> ManytoOne, OnetoMany관계 덕분에 가능한 것임
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // joinclumn이 있다는건 연관관계 주인이라는것..?
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 날짜 관련 어노테이션 매핑을 해주지 않고도 하이버네이트가 알아서 자동으로 지원해 준다.
    private LocalDateTime orderDate; // datetime : 시간이랑 분 까지 전부 있는 것

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    //==연관관계 편의 메서드==//
    // : 멤버를 세팅할 때 두개를 원자적으로 묶는 메서드를 만들기(실수를 방지하기 위해)
    // : 위치 - 컨트롤 하는 쪽에 두는게 좋다.
    public void setMember(Member member){
      this.member = member;
      member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 주문 생성에 대한 복잡한 비즈니스 로직을 완결시켜 버린다.                      // ... 쩜쩜쩜 문법?? 여러개를 넘길 수 있다.
    // createOrder하기 전에 이미 oOrderItem에서 재고를 까고 넘어온다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){           // 배송완료
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        // validation을 통과하면 order의 상태를 cancel로 바꿔주겠다.
        this.setStatus(OrderStatus.CANCEL);

        // roof를 돌리며 재고를 원상복구 시키기
        for(OrderItem orderItem : orderItems){ // this 생략가능
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){ // 현재 나한테는 정보가 없고, orderItem들을 전부 더하면 된다.
        int totalPrice = 0;
        for(OrderItem orderItem: orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
        // java8 문법에 의하면 이렇게도 가능
        /*
            return orderItems.stream()
               .mapToInt(OrderItem::getTotalPrice)
               .sum();
        */
    }
}
