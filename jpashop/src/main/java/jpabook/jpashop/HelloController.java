package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Thymleaf 를 위한 예시.
@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        // model에 데이터를 담아 뷰에 넘길 수 있다.
        model.addAttribute("data", "hello!!");
        // resources/templates/hello.html을 보여준다.
        return "hello";
    }
}
