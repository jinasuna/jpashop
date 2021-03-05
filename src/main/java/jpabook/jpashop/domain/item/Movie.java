package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity                     // 싱글 테이블 전략이라서
@DiscriminatorValue("M") // 저장이 될때 DB입장에서 구분할 수 있어야 해서 넣는 값
@Getter
@Setter
public class Movie extends Item{
    private String director;
    private String actor;
}
