package net.tislib.uiexpose.lib.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.serializer.JacksonSerializeModule;

public class ModelPublisher {
    private ObjectMapper objectMapper = new ObjectMapper();

    public ModelPublisher() {
        objectMapper.registerModule(new JacksonSerializeModule());
    }

    @SneakyThrows
    public void publish(Model model, PrintStream out) {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, model);
    }
}
