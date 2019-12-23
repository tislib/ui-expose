package net.tislib.uiexpose;

import net.tislib.uiexpose.lib.spring.annotations.ServiceOperator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final ServiceOperator serviceOperator;

    public HelloController(ServiceOperator serviceOperator) {
        this.serviceOperator = serviceOperator;
    }

    @RequestMapping("/")
    public String index() {
        serviceOperator.test();
        return "Greetings from Spring Boot!";
    }

}