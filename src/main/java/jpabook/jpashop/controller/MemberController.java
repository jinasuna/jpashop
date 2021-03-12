package jpabook.jpashop.controller;

import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
