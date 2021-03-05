package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity                     // 싱글 테이블 전략이라서
@DiscriminatorValue("B") // 저장이 될때 DB입장에서 구분할 수 있어야 해서 넣는 값
@Getter
@Setter
public class Book extends Item{
    private String author;
    private String isbn;

}
