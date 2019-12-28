package net.tislib.uiexpose.lib.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.util.Set;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.serializer.JacksonSerializeModule;

public class OutputPublisher {
    private ObjectMapper objectMapper = new ObjectMapper();

    public OutputPublisher() {
        objectMapper.registerModule(new JacksonSerializeModule());
    }

    @SneakyThrows
    public void publish(Set<ServiceInfo> serviceInfoList, PrintStream out) {
        objectMapper.writeValue(out, serviceInfoList);
    }
}
