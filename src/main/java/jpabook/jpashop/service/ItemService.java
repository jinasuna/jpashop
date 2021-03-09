package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 상품 repository에 위임만 하는 클래스 - 개발이 단순하다
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional // readOnly = false를 해줌
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // readOnly = true
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    // readOnly = true
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
