package net.tislib.uiexpose.demo1;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestService1CoreLibScannerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void serviceInfoValidation() throws Exception {
        mockMvc.perform(get("/api/api-descriptions"))
                .andExpect(jsonPath("$.services[0].name", is("TestService1")))
                .andExpect(jsonPath("$.services[0].group", is("test")))
                .andExpect(jsonPath("$.services[0].methods[0].name", is("test1")))
                .andExpect(jsonPath("$.services[0].methods[0].returnType", is("string")))
                .andExpect(jsonPath("$.services[0].methods[0].arguments[0].name", is("param1")))
                .andExpect(jsonPath("$.services[0].methods[0].arguments[1].name", is("param2")))
                .andExpect(jsonPath("$.services[0].methods[0].arguments[0].type", is("string")))
                .andExpect(jsonPath("$.services[0].methods[0].arguments[1].type", is("string")));
    }
}
