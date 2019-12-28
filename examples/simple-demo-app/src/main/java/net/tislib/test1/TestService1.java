package net.tislib.test1;

import net.tislib.uiexpose.lib.annotations.UIExpose;
import org.springframework.stereotype.Service;

@Service
@UIExpose(group = "test")
public class TestService1 {

    public String test1(String param1, String param2) {
        return param1 + param2;
    }

    public String test2(String param1, String param2, String param3) {
        return param1 + param2;
    }

    public String test3(String param1, String param2, String param3) {
        return param1 + param2 + param3;
    }

    public String test4(String param1, String param2, String param3) {
        return param1 + param2 + param3;
    }

}