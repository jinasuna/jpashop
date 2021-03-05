package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    /*@Autowired // 1. 필드 인젝션 - 문제점 : 바꿀수 있는 방법이 없다(필드이고, 프라이빗이고 해서)
    private MemberRepository memberRepository;*/

    // 2. setter 인젝션 - 장점 : 테스트코드 작성할 때 mock같은것을 직접 주입해 줄 수 있다(필드는 주입이 까다로움)
    //        치명적인 단점 : 실제 런타임에서는 setMemberRepository();해서 바꿀 일이 없다(굳이 필요 없음)
    /*
    @Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/

    // 3. 생성자 인젝션 - 권장 방법 : 생성 시점에 무엇을 의존하고 있는지 명확히 알 수 있다.
    // 생성자가 단 하나만 있는 경우에는 @Autowired 없어도 spring이 그 하나의 생성자에 자동으로 인젝션 해 준다.
    private final MemberRepository memberRepository; // 변경할 일 없기 때문에 final로 하는 것을 권장
    // final로 만들어 놓으면 컴파일 시점에 체크할 수 있다 ex)생성자 값 세팅 안하면 빨간불
    /*
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/

    // 4. lombok 적용 - AllargsConstructor : 필드의 모든것을 가지고 생성자를 만들어 준다(단점:일부setter불가능)
    // 5. RequiredArgsConstructor : final 필드만 가지고 생성자를 만들어 준다.

    // 회원 가입 - 쓰기에는 readOnly=false(true로 하면 데이터 변경이 안된다)
    @Transactional // 기본 옵션 false
    public long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }
    // 중복 회원 검증 - 이렇게 검증을 하더라도 멀티스레드 상황을 고려해서 DB에 name을 유니크 제약조건을 걸어 놓는것이 좋다
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){ // EXCEPTION 발생시키기
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    // 회원 전체 조회 - 읽기에는 readOnly=true
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    // 하나의 회원 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}