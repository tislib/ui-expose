package net.tislib.uiexpose.lib.spring.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ServiceOperator {

    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceOperator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    //    @PostConstruct
    @SneakyThrows
    public void test() {
        String dataPath = locateDataPath();

        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(UIExpose.class);

        List<ServiceInfo> serviceData = beanMap.entrySet()
                .stream()
                .map(item -> mapService(item.getKey(), item.getValue()))
                .collect(Collectors.toList());

        objectMapper.writeValue(new File(dataPath + "/services.json"), serviceData);

    }

    private ServiceInfo mapService(String serviceName, Object serviceBean) {
        return new ServiceInfo();
    }

    private String locateDataPath() {
        String userDir = System.getProperty("user.dir");
        String buildDir = userDir + "/build";
        new File(buildDir + "/ui-expose").mkdirs();
        return buildDir + "/ui-expose";
    }

}
