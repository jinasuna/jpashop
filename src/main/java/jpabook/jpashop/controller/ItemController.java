package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        // 방법1. set메소드로 객체 초기화 -> 좋지 않음
        // createBook해서 파라미터 넘기거나 하는 방식이 더 나은 설계
        // setter 다 제거하고 static생성자 메소드로 의도에 맞게 생성해서 사용하는 것이 가장 깔끔한 설계
        // 예제라서 setter를 열어놔야 편하게 할수있는 부분이 많아서 우선 열어놨다.

        itemService.saveItem(book);
        // return "redirect:/items"; // 저장된 책 목록으로 바로 가버리고 싶다!
        return "redirect:/"; // 우선은 그냥 이렇게(책 목록이 아직 없음)
    }
    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }
    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        // 캐스팅 하는 것이 좋지는 않은데 예제를 간단하게 하기 위해 캐스팅 사용
        Book item = (Book) itemService.findOne(itemId);
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(form.getName());
        form.setPrice(form.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{ItemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){
        // ※ 아래의 코드는 예시를 위한 것일 뿐 원래는 컨트롤러에서 엔티티를 생성하지 않는 것이 좋다!
// 수정 방법 1.
        // 객체는 새로운 객체인데
//        Book book = new Book();
        // id가 이미 세팅이 되어 있다.
        // 뭔가 JPA에 한번 들어갔다 나온 애들이라는 뜻
        // 식별자가 이미 DB에 있는 이런 객체들 : 준영속 상태의 객체 -> book
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);

// 더 나은 설계 : 어설프게 엔티티를 파라미터로 사용하지 않고 정확히 내가 필요한 데이터(값)만 딱 딱 받은 것
          itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

          // 업데이트할 데이터가 많다면?! => 서비스 계층에 DTO를 하나 만들자.

          return "redirect:/items";
    }
}
