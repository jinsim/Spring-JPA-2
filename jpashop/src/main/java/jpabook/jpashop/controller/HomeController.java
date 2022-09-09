package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

//    Logger log = LoggerFactory.getLogger(getClass()); // SLF4J 로그를 찍을 수 있다. 롬복의 @Slf4j로 대체 가능

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
