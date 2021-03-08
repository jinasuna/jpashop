package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// JPA의 모든 데이터 변경이나 로직들은 transaction안에서 다 실행되어야 한다.(지연 로딩 등등을 위해서라도)
@Service   // javax와 스프링이 제공하는 transaction이 있다.
// -> 우리는 이미 spring이 제공하는 각종 기능들을 사용하고 있기 때문에 스프링 것으로 사용하는 걸로 한다(쓸 수 있는 옵션들이 많아짐)
@Transactional(readOnly = true) // 해당 클래스의 public메소드에 이 설정이 전부 적용된다.
// 단 해당 서비스가 커맨드성이 강해 쓰기를 주로 한다고 하면 이 어노테이션을 없애고 메소드 단위로 설정하는 것이 바람직하다.
// 4. @AllArgsConstructor // 클래스 멤버 필드의 모든것을 가지고 생성자를 그대로 똑같이 만들어 준다.
@RequiredArgsConstructor // 5. final 필드만 가지고 생성자 정의
public class MemberService {
    /*@Autowired // 스프링이 스프링 빈에 등록되어 있는 MemberRepository를 주입해 준다. -> 필드 인젝션
    // 1. 필드 인젝션 - 문제점 : 바꿀수 있는 방법이 없다(필드이고, 프라이빗이고 해서). 테스트를 하거나 할때 바꿔줘야 할수도 있는데...
    private MemberRepository memberRepository;*/

    // 2. setter 인젝션 - 장점 : 테스트코드 작성할 때 mock같은것을 직접 주입해 줄 수 있다(필드는 주입이 까다로움)
    //        치명적인 단점 : 실제 어플리케이션 돌아가는 시점에 누군가 setMemberRepository()해서 바꿀 수 있는데, 이것이 위험하다.
    //        개발자 입장에서 생각해보면 실제 런타임에서는 setMemberRepository();해서 개발하는 중간에 바꿀 일이 없다(굳이 필요 없음), 어플리케이션 로딩 시점에 조립이 다 끝나버린다.
    /*
    @Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/

    // 권장 방법
    // 3. 생성자 인젝션 - 스프링이 뜰 때 생성자에서 레포지토리를 인젝션을 해준다. 한번 생성할때 완성이 되기 때문에 중간에 멤버리포지토리를 바꿀 일이 없다.
    // : 생성 시점에 무엇을 의존하고 있는지 명확히 알 수 있다.
    // 테스트코드에서 서비스를 생성할 때 매개변수 부분에 빨간줄이 떠서 무엇을 주입해야 하는지 여부를 확실히 알 수 있다.
    // 생성자가 단 하나만 있는 경우에는 @Autowired와 생성자 설정이 없어도 spring이 그 하나의 생성자에 자동으로 인젝션 해 준다.
    private final MemberRepository memberRepository; // 변경할 일 없기 때문에 final로 하는 것을 권장
    // final로 만들어 놓으면 컴파일 시점에 체크할 수 있다 ex)생성자 값 세팅 제대로 안하면 윗줄 코드에 빨간불
    /*
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/

    // lombok 활용
    // 4. AllargsConstructor : 필드의 모든것을 가지고 생성자를 만들어 준다(단점:일부setter불가능)
    // 5. RequiredArgsConstructor : final이 있는 필드만 가지고 생성자를 만들어 준다.
    // -> injection하면서 세팅 끝날 멤버들을 final로 설정해 두고, 중간에 필드가 혹시 필요한 나머지 멤버의 경우에는 setter등 사용할 수 있도록 해준다.

    // 회원 가입 - 쓰기에는 readOnly=false(★★★true로 하면 데이터 변경이 안된다)
    @Transactional // 기본 옵션 false, 메소드에 따로 설정을 하면 이 설정이 우선권을 가져서 적용된다.
    public long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member); // em.persist : 영속성 컨텍스트에 Member객체를 올리기 위해 엔티티에 값이 박혀 들어간다.
        // 영속성 컨텍스트에 값을 입력하려면 (key, value)형태가 되어야 하는데 pk값 자체가 key가 되면서 엔티티에 값도 채워준다. (아직 DB에 들어간 시점이 아니어도)
        return member.getId(); // DB에 들어간 상태가 아니어도 값을 꺼낼 수 있다. -> 항상 값이 있다는 것이 보장이 됨
        // id라도 돌려줘야 뭐가 저장되었는지 알 수 있으니!
    }

    // 중복 회원 검증
    // 문제점 : WAS가 동시에 여러개가 뜨면 어떤 녀석이 아직 DB에 아직 memberA가 둘이 동시에 DB에 insert를 하게 되면 둘이 동시에 vali~메소드를 통과하게 되고 .save()로직을 둘다 동시에 호출하게 된다.
    // -> 동시에 memberA라는 이름의 회원이 두명 가입하게 된다. => 비즈니스 로직이 이렇게 있다고 하더라도 실무에서는 한번 더 최후의 방어를 해야 한다.
    // 멀티 스레드와 같은 상황을 고려해서 DB에 name을 유니크 제약조건을 걸어 놓는것이 좋다.
    private void validateDuplicateMember(Member member) {
        // 방법1. 입력하려는 멤버의 이름을 가지고 찾아서 있는 이름인지 확인하기, 이름으로 찾았는데 비어있지 않으면(select가 되면)에러 처리
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){ // EXCEPTION 발생시키기(이미 있는 member일 경우)
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        // 방법2. 새로 입력하려는 멤버가 있는지 세는것으로 조회해서 그 수가 0보다 크면 문제가 있다 라는 로직이 좀더 최적화된 스타일
    }
    // 회원 전체 조회 - 읽기를 할 때에는 readOnly=true (JPA가 조회하는 곳에서 성능을 최적화 시켜줌)
    // -> DB에게 이거 읽기 전용이니까 리소스 너무 많이 쓰지 말고 단순히 읽기용 모드로 해서 DB야 니가 읽어 라고 해주는 드라이버들도 있음
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    // 하나의 회원 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}