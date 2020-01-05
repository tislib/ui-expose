package net.tislib.uiexpose.demo1;

import net.tislib.uiexpose.lib.annotations.UIExpose;
import org.springframework.stereotype.Service;

@Service
@UIExpose(group = "test")
public class TestService1 {

    public String test1(String param1, String param2) {
        return param1 + param2;
    }

    public Person createDummyPerson(String name, String surname) {
        Person person = new Person();
        person.setName(name);
        person.setSurname(surname);
        person.setProduct(new PersonProduct());
        person.getProduct().setPName(name + name);
        return person;
    }
}
