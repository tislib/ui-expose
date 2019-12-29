package net.tislib.uiexpose;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.data.Value;
import net.tislib.uiexpose.lib.exporer.LocalServiceExplorer;
import net.tislib.uiexpose.lib.exporer.ServiceMethodLocator;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UIExposeRestService {

    private LocalServiceExplorer serviceExplorer;
    private ServiceMethodLocator serviceMethodLocator;
    private final ApplicationContext applicationContext;
    private Map<Class<?>, ?> serviceClassBeanMap;
    private Set<ServiceInfo> serviceInfoList;

    @PostConstruct
    public void init() {
        this.serviceExplorer = new LocalServiceExplorer();
        serviceExplorer.loadExposedServices();
        this.serviceMethodLocator = new ServiceMethodLocator(serviceExplorer.getExposedServices());
        this.serviceMethodLocator.loadMethodInfo();
        this.serviceClassBeanMap = serviceExplorer.getExposedServices()
                .stream()
                .collect(Collectors.toMap(item -> item, applicationContext::getBean));

        this.serviceInfoList = new ServiceProcessorImpl().process(serviceExplorer.getExposedServices());
    }

    public <T> Object execute(String serviceName, String methodName, RequestParamsWrapper requestParamsWrapper, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Object serviceBean = serviceClassBeanMap.get(serviceMethodLocator.locateService(serviceName));
        Method method = this.serviceMethodLocator.locateMethod(serviceName,
                methodName, requestParamsWrapper.getValues()
                        .stream()
                        .map(Value::getType)
                        .collect(Collectors.toList())
        );
        return serviceMethodLocator.invokeWithValues(serviceBean, method, requestParamsWrapper.getValues());
    }

    public Set<ServiceInfo> getApiDescriptions() {
        return serviceInfoList;
    }
}
