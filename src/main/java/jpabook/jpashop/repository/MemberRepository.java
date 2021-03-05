package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    // 저장하는 코드
    public Long save(Member member){
        em.persist(member);
        return member.getId();
        // member를 반환하지 않고 Id를 반환하는 이유 : 커맨드와 쿼리를 분리해라 라는 원칙에 의해
        // 저장을 하고 나면 가급적 member(커맨드성이 있음)에 대한 리턴값을 만들지 않는다.
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
