package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 인강 자료 그림의 parent와 child는 계층 구조를 이렇게 나타낼 수 있다, 매핑할수 있다 라는 것을 보여줌
// 자기 자신을 셀프로 매핑하기..? 그 방식은 관계형 DB와 똑같다..?
@Entity
@Getter @Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    // 카테고리 이름
    private String name;

    // jpa를 쓰는데 category구조를 어떻게 하지? category구조가 계층구조라서 쭉 내려가는데..아래에서 위로 보고, 위에서 아래로 보고 다 가능해야...

    // Category와 List<Item> items의 대 다 매핑을 위한 어노테이션 설정
    // 다 대 다 : Category도 List로 Item을 가지고 Item도 List로 Category를 갖는다.
    @ManyToMany
    // JoinTable이 필요! 그림에서 봤던 중간테이블을 만들고 그걸로 매핑을 해줘야 해서
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"), // 중간테이블에 있는 category_id
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 중간테이블의 item쪽으로 들어가는 item_id를 매핑
    private List<Item> items = new ArrayList<>();

    // '나' 타입의 내 부모(...?)
    @ManyToOne(fetch = FetchType.LAZY) // '내' 부모니까 -> 부모가 one 자식이 many인 입장
    @JoinColumn(name = "parent_id") // join으로 해결해줌
    private Category parent;

    // 부모가 자식(카테고리)을 여러개 가질 수 있다
    @OneToMany(mappedBy = "parent") // 바로 위의 객체를 mappedBy함
    private List<Category> child = new ArrayList<>();

    // 연관관계 편이 메소드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
