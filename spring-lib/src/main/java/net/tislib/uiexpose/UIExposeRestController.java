package net.tislib.uiexpose;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.data.Value;
import net.tislib.uiexpose.lib.serializer.JacksonSerializeModule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UIExposeRestController {

    private final UIExposeRestService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new JacksonSerializeModule());
    }

    @RequestMapping("/{serviceName}/{methodName}")
    @SneakyThrows
    public void execute(@RequestBody String body,
                        @PathVariable("serviceName") String serviceName,
                        @PathVariable("methodName") String methodName,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        Object res = service.execute(serviceName, methodName, objectMapper.readValue(body, RequestParamsWrapper.class), request, response);

        objectMapper.writeValue(response.getOutputStream(), res);
    }

}
