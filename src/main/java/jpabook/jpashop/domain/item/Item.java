package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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
@Getter @Setter // 편의상 setter를 넣어 놓았지만 stockQuantity의 내용을 변경시킬 일이 있으면 setter를 사용하는 것이 아니라 핵심 비즈니스 메소드를 가지고 변경하는 것이 바람직하다.(객체지향적)
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

    // ==비즈니스 로직 추가== //
    // 재고를 늘리고 줄이도록 할 것이다.
    // 도메인 주도 설계를 할 때 엔티티 자체에서 해결할 수 있는 내용들은 엔티티 안에 비즈니스 로직으로 넣는 것이 좋다 -> 객체 지향적

    /**
     * stock 증가시키는 메소드
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    /**
     * stock 감소시키는 메소드
     * 상품주문_재고수량초과() 테스트를 제대로 하려면 removeStock()에 대한 단위 테스트가 필요하다.
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
