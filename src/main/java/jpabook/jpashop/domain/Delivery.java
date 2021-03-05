package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// order와 delivery가 있을 때 delivery는 거의 order를 바라보면서 찾게 되므로,
// 좀더 접근을 많이 하게 되는 order클래스(테이블)쪽에 외래키를 두는 것이 좋다.
// 이 때 연관관계 주인은 order에 있는 delivery를 연관관계 주인으로 주면 된다.

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    // mappedBy 라는 것은 연관관계 주인이 아니라 거울 이라는 뜻
    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // ORDINAL 절대 쓰면 안되고 꼭 String을 써야 한다.
    private DeliveryStatus status; //READY, COMP
}
