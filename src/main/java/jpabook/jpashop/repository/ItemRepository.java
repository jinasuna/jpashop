package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null){ // item은 처음 저장할 때 id가 없다. -> 완전히 새로 생성하는 객체라는 뜻
            em.persist(item); // jpa가 제공하는 persist를 사용(신규로 등록)
        } else { // DB에 등록된걸 한 번 가져온 적 있다는 뜻(id값이 이미 있을 경우)
            Item merge = em.merge(item);// update 비슷한거 => 병합!!merge 이었음
            // 변경 감지와 다른 점!
            // item은 영속성 컨텍스트로 바뀌지 않는다.
            // 반환된 Item 객체인 merge만이 영속성 컨텍스트에서 관리되고
            // 기존에 파라미터로 넘어온 item은 영속성 컨텍스트에서 관리되지 않는다.
            // item과 merge는 다른 아이!
            // => 혹시 더 사용할 일이 있다면 merge를 써야 한다.
        }
    }
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
