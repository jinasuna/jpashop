package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 기본 item을 만들고, 이 속성을 구현하는 클래스 3개를 만들어서 상속관계 매핑을 해야함!!
// 상속관계 매핑은 상속관계 전략을 잡아줘야 하는데, 부모 클래스에서 잡아줘야 한다.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 싱글 테이블 전략을 사용함을 나타냄
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id") // OrderItem의 (name = "item_id")로 매핑되어 있는 Item클래스 객체 item과 연결이 됨
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
