package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello") // hello라는 url로 오면 이 컨트롤러(메소드)가 호출되겠다
    public String hell(Model model){
        model.addAttribute("data","hello!!");
        return "hello"; // 화면 이름을 return해주는것(자동으로 뒤에 .html이 붙게 된다)
    }
}
