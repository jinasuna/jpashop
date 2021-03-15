package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        // Controller에서 view로 넘어갈 때 여기에 데이터를 실어서 보낸다.
        // controller가 화면으로 이동할 때 memberForm이라는 빈 껍데기 멤버 객체를 가지고 간다.
        // 가지고 가는 이유 : 빈 화면이니까 아무것도 없을수도 있는데,
        // validation 같은 것들을 해주기 때문에 빈 껍데기라도 도들고 간다.
        model.addAttribute("memberForm", new MemberForm()); // 이렇게 하면 화면에서 이 객체에 접근할 수 있게 된다!

        // 화면을 정해주기
        return "members/createMemberForm";
    }

    // 등록할 때에는 form 객체를 썼는데데
   @PostMapping("/members/new")             // Form에 오류가 있으면 원래는 컨트롤러에 코드가 안넘어가고 튕겨버리는데
                                                // Validate와 함께 BindingResult가 있으면 오류가 result에 담겨서 아래의 코드가 실행이 된다.
    public String create(@Valid MemberForm form, BindingResult result){

        // result를 보면 에러에 대한 데이터를 찾을 수 있는 메서드가 많다. 에러가 있으면 다시 form으로 보내버렸다.
        // spring이 BindingResult를 form에 끌고와서 쓸 수 있게 도와준다.

        // spring이 thymeleaf와 강하게 integration되어 있어서 이렇게 하면 spring이 BindingResult를 화면까지 끌고 가 준다 -> 어떤 에러가 있는지 화면에 뿌릴 수 있다.
        if(result.hasErrors()){
            return "members/createMemberForm";
        }

       Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
       Member member = new Member();
       member.setName(form.getName());
       member.setAddress(address);

       memberService.join(member);
       // 저장이 끝나면 어디로 갈래? 저장이 끝나고나서 재로딩되거나 하면 안좋다
       return "redirect:/"; // 리다이렉트로 폼으로 보내버리기 -> 첫번째 페이지로 넘어가거나 한다
    }

    @GetMapping("/members")
    public String list (Model model){ // Model이라는 객체를 이용해 화면에 내용을 전달하게 된다
        // 김영한쌤 : Model에 addAttribute할 때에는 명확하게 담기는 객체를 잘 참조할 수 있도록 변수를 받아서 넣는게 편하시다

        // 뿌릴 때에는 Member Entity를 그대로 뿌리고있네?!
        // 현재는 단순한 상황이라 이렇게 했지만, 더 복잡해지면 DTO로 변환해서 화면에 꼭 필요한 데이터만 가지고 출력하는 것을 권장
        // 또한, API를 만들 때에는 이유를 불문하고 절대 Entity를 넘기면(외부로 반환하면) 안된다.
        // => API라는 것은 스펙인데, Entity의 로직을 변화했는데 API스펙이 변하는 상황이 생기면 안된다.
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        // model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }
}
