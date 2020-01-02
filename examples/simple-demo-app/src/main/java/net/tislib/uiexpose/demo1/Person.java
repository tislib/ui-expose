package net.tislib.uiexpose.demo1;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Person {
    private String name;
    private String surname;

    private URL url;
    private Date date;
    private Timestamp timestamp1;
    private Instant instant1;
    private Map<String, List<String>> map1;

    private PersonProduct product;
}
