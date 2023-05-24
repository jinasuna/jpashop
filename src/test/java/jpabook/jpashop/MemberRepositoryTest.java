package jpabook.jpashop;

<<<<<<< HEAD
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
=======

>>>>>>> eb07265 (2023-0525ver commit)
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class) // JUnit에게 스프링과 관련된 걸로 테스트 할거야 알려주기
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{
        /*  실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발 강의
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member); // member를 save하면
        Member findMember = memberRepository.findOne(saveId);

        // then save한게 잘 저장되었는지 검증
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // pass
*/


        }

    }


