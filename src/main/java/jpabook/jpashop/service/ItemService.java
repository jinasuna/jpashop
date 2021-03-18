package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
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

    // ★★★★★★준영속 객체의 변경 감지에 대해 알아보자!★★★★★★
    @Transactional
    public void updateItem(Long itemId, /*Book param*/ String name, int price, int stockQuantity){
        // 영속성 컨텍스트에서 id를 기반으로 실제DB에 있는 영속 상태의 엔티티를 찾아왔다
        // em.merge(item)으로 넘긴 파라미터의 값(여기서는 Book param)으로
        Item findItem = itemRepository.findOne(itemId);

        // 찾아온 것의 값들(모든 데이터)을 다 바꿔치기 해버린다.
        // 바꿔치기가 되니까 트랜잭션 커밋 될 때 반영이 된다.
//      findItem.setPrice(param.getPrice());
//      findItem.setName(param.getName());
//      findItem.setStockQuantity(param.getStockQuantity());

        // 방법2.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        // 쉽게 말해서 merge는 내가 한땀한땀 짠 코드를 JPA가 한줄(em.merge())로 처리해주는 것이다.
        // 그 후 바뀐 데이터를 반환 해준다.
        // return findItem; 그런데 사실 return할것도 없다. 어차피 update 날린 거니까!

        // 위의 코드들은 em.merge(item);와 완전히 동일한 코드다.

        // 이럴 필요 없다. 아무것도 호출할 필요 없다.
        // itemRepository.save(findItem);
        // => findItem으로 찾아온 애들은 영속 상태이다.
        // 값을 세팅한 다음에 아무것도 해주지 않아도 springboot의 Transactional에 의해서 메소드가 끝난 후 Transaction이 커밋
        // 커밋을 하면 JPA가 flush를 해 주고 hibernate가 영속성 컨텍스트에 있는 엔티티 중 변경된 애가 누구지 하고 찾는다.
        // 찾았는데 set으로 바뀐 내용이 있으면 바뀐 값을 DB에 날려서 update를 쳐버린다.
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
