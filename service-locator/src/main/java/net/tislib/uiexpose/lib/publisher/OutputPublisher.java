package net.tislib.uiexpose.lib.publisher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.data.Type;

public class OutputPublisher {
    private ObjectMapper objectMapper = new ObjectMapper();

    public OutputPublisher() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Type.class, new JsonSerializer<Type>() {
            @Override
            public void serialize(Type value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
                jgen.writeString(value.toString());
            }
        });
        objectMapper.registerModule(module);
    }

    @SneakyThrows
    public void publish(Set<ServiceInfo> serviceInfoList, PrintStream out) {
        objectMapper.writeValue(out, serviceInfoList);
    }
}
