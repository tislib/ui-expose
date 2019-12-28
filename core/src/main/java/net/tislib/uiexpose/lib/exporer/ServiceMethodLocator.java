package net.tislib.uiexpose.lib.exporer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.tislib.uiexpose.lib.data.MethodInfo;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.data.Type;
import net.tislib.uiexpose.lib.data.Value;
import net.tislib.uiexpose.lib.processor.ServiceProcessor;
import net.tislib.uiexpose.lib.processor.ServiceProcessorImpl;

@RequiredArgsConstructor
public class ServiceMethodLocator {
    private final Set<Class<?>> exposedServices;
    private final ServiceProcessor serviceProcessor = new ServiceProcessorImpl();
    private Map<String, MethodEntry> methodEntryMap;
    private Map<String, Class<?>> serviceEntryMap;

    public void loadMethodInfo() {
        this.serviceEntryMap = this.serviceProcessor.processMapped(exposedServices)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        item -> item.getValue().getName(), item -> item.getKey()
                ));

        this.methodEntryMap = this.serviceProcessor.processMapped(exposedServices)
                .entrySet()
                .stream()
                .flatMap(item -> item.getValue().getMethods().stream().map(
                        methodInfo -> MethodEntry.builder()
                                .serviceClass(item.getKey())
                                .serviceInfo(item.getValue())
                                .methodInfo(methodInfo)
                                .method(methodInfo.getMethod())
                                .build()
                ))
                .collect(Collectors.toMap(
                        this::getServiceMethodKey, item -> item
                ));
    }

    private String getServiceMethodKey(MethodEntry item) {
        return item.getServiceInfo().getName()
                + "-" + item.getMethodInfo().getName()
                + "-" + item.getMethodInfo().getArguments()
                .values()
                .stream()
                .map(Type::toString)
                .collect(Collectors.joining(","));
    }

    public Method locateMethod(String serviceName, String methodName, List<? extends Type<?>> types) {
        return methodEntryMap.get(serviceName + "-" + methodName + "-" + types.stream()
                .map(Type::toString)
                .collect(Collectors.joining(","))).getMethod();
    }

    public Object invokeWithValues(Object serviceInstance, Method method, List<Value<?>> values) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(serviceInstance, values.stream().map(Value::getValue).toArray());
    }

    public Class<?> locateService(String serviceName) {
        return serviceEntryMap.get(serviceName);
    }


    @Data
    @Builder
    public static class MethodEntry {
        private Class<?> serviceClass;
        private ServiceInfo serviceInfo;
        private MethodInfo methodInfo;
        private Method method;
    }
}
