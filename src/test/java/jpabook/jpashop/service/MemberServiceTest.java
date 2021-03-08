package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.Assert.*;

// 순수한 단위테스트가 아니라 완전히 스프링을 인티그레이션 하는 테스트
// jpa가 DB 까지 도는 것을 보자(메모리 모드로 DB까지 엮어서 테스트 할것)
// RunWith와 SpringBootTest가 있어야 스프링을 인티그레이션 해서 스프링부트를 딱 올려서 테스트를 할 수 있다.
@RunWith(SpringRunner.class) // JUnit 실행할때 스프링이랑 엮어서 실행할래! 라는 뜻
@SpringBootTest // springBoot를 띄운 상태로 테스트를 하려면 이 어노테이션이 필요하다. 이거 없으면 아래쪽 @Autowired 다 실패한다.
// 스프링 컨테이너 안에서 테스트를 돌리는것!!!
@Transactional // 데이터를 변경해야 하기 때문에(이 어노테이션이 있어야 rollback이 된다)
// JPA는 같은 트랜잭션 안에서 같은 엔티티(pk)값이 똑같으면 같은 영속성 컨텍스트에서 똑같은 녀석이 관리가 된다.
// spring에서의 트랜잭셔널은 ★★★테스트 케이스에 있을 경우★★★ 기본적으로 commit을 안하고 롤백을 해버린다.
// -> 롤백을 하는 이유 : 테스트는 반복적으로 해야 하기 때문에
// spring이 롤백을 해버리면 jpa 입장에서는 insert문을 db에 날릴 이유가 없어진다.
// (롤백을 한다는것 자체가 DB에 있는 것을 다 버린다는 뜻이기 때문에)
// 영속성 컨텍스트가 flush를 하지 않는다.

//@Rollback(false) // 1. 이렇게 해야 결과를 눈으로 볼 수 있다.
public class MemberServiceTest {

    // 필요한것 두가지가 있다 : 서비스와 레포지토리
    @Autowired MemberService memberService; // 테스트 케이스이기 때문에 다른 녀석들이 이녀석을 참조할게 없고, 간단히 이렇게만 해줘도 된다.
    @Autowired MemberRepository memberRepository;

    // 눈으로 결과보기 ★★★★★★★2번 방법
    // @Autowired EntityManager em;

    @Test
 //   @Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member); // 영속성 컨텍스트에 member객체가 들어간다.

        //then
        // 맞는지 확인하기(여기서 만든 멤버, 멤버레포지토리에서 꺼내온 멤버)
        // em.flush();  // 영속성 context에 있는 내용이 DB에 쿼리가 강제로 나간다.
        assertEquals(member, memberRepository.findOne(savedId));

    }
    
    @Test /*2번 방법 */ (expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        /* ★★★★★★1번 방법
        memberService.join(member1);
        try{
            memberService.join(member2);
        }catch(IllegalStateException e){
            return;
        }
        memberService.join(member2); // 예외가 발생해야 한다.
        */
        memberService.join(member1);
        memberService.join(member2);

        //then
        fail("예외가 발생해야 합니다.");
    }

}