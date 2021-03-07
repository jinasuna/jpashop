package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    // 식별자 // 자동생성
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;

    @Embedded
    private Address address;
            // 읽기 전용이 되었다는 뜻 -> 여기에 값을 넣는다고 해도 DB테이블의 foreign키 값이 변하지 않는다.
            // 나는 주인이 아니에요. 연관관계 거울이에요. orders테이블에 있는 member필드에 의해 매핑된거야.
    @OneToMany(mappedBy = "member") // 하나의 회원이 여러개의 상품을 주문
    private List<Order> orders = new ArrayList<>();
    // 컬렉션은 필드에서 바로 초기화 하는것이 null문제에서 안전하다.
}
