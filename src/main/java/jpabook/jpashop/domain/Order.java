package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne  // order랑 member는 다대 일 관계
    @JoinColumn(name = "member_id") // foreign키 이름이 member_id가 되는 것
    private Member member; // 주문한 회원에 관한 정보
    // 여기에 값을 매핑하면 member_id라는 foreign키 값이 다른 멤버로 변경된다.

    // order_id라는 이름의 컬럼을 OrderItem에서 Order클래스 객체 order를 만들어 가져왔기 때문에
    @OneToMany(mappedBy = "order")  // OrderItem클래스가 order라는 이름으로 매핑되어 생김..? -> ManytoOne, OnetoMany관계 덕분에 가능한 것임
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne // joinclumn이 있다는건 연관관계 주인이라는것..?
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 날짜 관련 어노테이션 매핑을 해주지 않고도 하이버네이트가 알아서 자동으로 지원해 준다.
    private LocalDateTime orderDate; // datetime : 시간이랑 분 까지 전부 있는 것

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]
}
