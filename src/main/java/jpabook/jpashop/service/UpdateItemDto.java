package jpabook.jpashop.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateItemDto {
    // 유지보수하기 더 나은 설계 방식
    // ItemService에서는
    // @Transactional
    // public void updateItem(Long itemId, UpdateItemDto itemDto){}
    // 이런 식으로 만들어서 update해주기
}
