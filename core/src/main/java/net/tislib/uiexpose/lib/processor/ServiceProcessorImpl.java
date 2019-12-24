package net.tislib.uiexpose.lib.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import net.tislib.uiexpose.lib.data.MethodInfo;
import net.tislib.uiexpose.lib.data.ServiceInfo;
import net.tislib.uiexpose.lib.data.Type;

public class ServiceProcessorImpl implements ServiceProcessor {
    @Override
    public Set<ServiceInfo> process(Set<Class<?>> exposedServices) {
        return exposedServices.stream().map(this::process).collect(Collectors.toSet());
    }

    @Override
    public Map<Class<?>, ServiceInfo> processMapped(Set<Class<?>> exposedServices) {
        return exposedServices.stream().collect(Collectors.toMap(item -> item, this::process));
    }

    public ServiceInfo process(Class<?> serviceClass) {
        UIExpose annotation = serviceClass.getAnnotation(UIExpose.class);

        ServiceInfo serviceInfo = new ServiceInfo();

        serviceInfo.setName(serviceClass.getSimpleName());
        serviceInfo.setGroup(annotation.group());

        serviceInfo.setMethods(Arrays.stream(serviceClass.getDeclaredMethods()).map(this::processMethod).collect(Collectors.toSet()));

        return serviceInfo;
    }

    private MethodInfo processMethod(Method method) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(method.getName());
        methodInfo.setMethod(method);
        methodInfo.setReturnType(resolveType(method.getReturnType()));
        methodInfo.setArgumentTypes(Arrays.stream(method.getParameterTypes()).map(this::resolveType).collect(Collectors.toList()));
        return methodInfo;
    }

    private Type resolveType(Class<?> returnType) {
        switch (returnType.getSimpleName()) {
            case "String":
                return Type.STRING_TYPE;
        }
        return null;
    }
}
