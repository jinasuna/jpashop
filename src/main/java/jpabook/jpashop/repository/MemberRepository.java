package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// 엔티티 매니저는 Autowired로 등록이 안되고 PersistenceContext로 표준 어노테이션이 있어야 인젝션이 되는데
// 스프링 부트(spring-data-jpa)가 Autowired로 인젝션 되게 지원을 해준 것이라는 것을 명심할 것!★★★★★★
@Repository
@RequiredArgsConstructor // 리퍼지토리에 엔티티매니저를 생성자로 인젝션 한것임
public class MemberRepository {

    // spring boot 라이브러리의 spring-data-jpa를 사용하면 PersistenceContext어노테이션을 @Autowired이렇게 바꿀 수 있다.
    // @Autowired 생성자가 단 하나만 있는 경우에는 @Autowired 없이도 spring이 생성자의 멤버 필드를 bean으로 인식해 준다.
    // @PersistenceContext // 엔티티 매니저를 주입해주는 어노테이션(스프링이 알아서 해준다)
    private final EntityManager em; // 변경할 일 없기 때문에 final로 하는 것을 권장

    // 멤버레포지토리도 멤버서비스 한것과 마찬가지로 생성자 인젝션을 할 수 있다.
    /* @Autowired 생성자가 단 하나만 있는 경우에는 생성자 정의 없이도 spring이 그 하나의 생성자에 자동으로 인젝션 해 준다.
    public MemberRepository(EntityManager em){
        this.em = em;
    }*/

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
    // JPQL과 SQL의 차이점 : JPQL은 엔티티 객체를 대상으로 쿼리
    public List<Member> findAll(){ // JPQL을 작성해야
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){ // where문이 들어간 JPQL :XX -> 파라미터를 바인딩
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // 특정 이름에 해당하는 레코드를 조회
                .getResultList();
    }

}
