package net.tislib.uiexpose.lib.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import net.tislib.uiexpose.lib.data.MethodArgument;
import net.tislib.uiexpose.lib.data.MethodModel;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.data.ServiceModel;
import net.tislib.uiexpose.lib.data.UIExposeType;

@RequiredArgsConstructor
public class ServiceProcessorImpl implements ServiceProcessor {

    private final BeanProcessor beanProcessor;

    @Override
    public Model process(Set<Class<?>> exposedServices) {
        Set<ServiceInfo> serviceList = exposedServices.stream().map(this::process).collect(Collectors.toSet());
        Set<Class<?>> types = resolveTypes(serviceList);

        Model.ModelBuilder modelBuilder = Model.builder();
        modelBuilder.services(serviceList.stream()
                .map(ServiceInfo::getServiceModel)
                .collect(Collectors.toSet()));
        beanProcessor.process(modelBuilder, types);

        return modelBuilder.build();
    }

    private Set<Class<?>> resolveTypes(Set<ServiceInfo> serviceList) {
        return serviceList.stream()
                .flatMap(serviceInfo -> serviceInfo.getMethods().stream())
                .flatMap(method -> {
                    Set<Class<?>> types = new HashSet<>();
                    Class<?> returnType = method.getMethod().getReturnType();
                    types.add(returnType);
                    types.addAll(Arrays.asList(method.getMethod().getParameterTypes()));
                    return types.stream();
                }).collect(Collectors.toSet());
    }

    @Override
    public Map<Class<?>, ServiceInfo> processMapped(Set<Class<?>> exposedServices) {
        return exposedServices.stream().collect(Collectors.toMap(item -> item, item -> process(item)));
    }

    public ServiceInfo process(Class<?> serviceClass) {
        UIExpose annotation = serviceClass.getAnnotation(UIExpose.class);

        ServiceModel serviceModel = new ServiceModel();

        serviceModel.setName(serviceClass.getSimpleName());
        serviceModel.setGroup(annotation.group());

        Set<MethodInfo> methodInfos = Arrays.stream(serviceClass.getDeclaredMethods()).map(this::processMethod).collect(Collectors.toSet());

        serviceModel.setMethods(methodInfos.stream().map(MethodInfo::getMethodModel).collect(Collectors.toSet()));

        return ServiceInfo.builder()
                .serviceClass(serviceClass)
                .serviceModel(serviceModel)
                .methods(methodInfos)
                .build();
    }

    private MethodInfo processMethod(Method method) {
        MethodModel methodModel = new MethodModel();
        methodModel.setName(method.getName());
        methodModel.setReturnType(resolveType(method.getReturnType()));
        methodModel.setArguments(Arrays.stream(method.getParameters())
                .map(item -> MethodArgument.create(item.getName(), this.resolveType(item.getType())))
                .collect(Collectors.toList()));

        return MethodInfo.builder()
                .method(method)
                .methodModel(methodModel)
                .build();
    }

    private UIExposeType<?> resolveType(Class<?> returnType) {
        return new DefaultTypeProcessor().locateUiExposeType(returnType);
    }

    @Data
    @Builder
    public static class ServiceInfo {
        ServiceModel serviceModel;
        Set<MethodInfo> methods;
        Class<?> serviceClass;
    }

    @Data
    @Builder
    public static class MethodInfo {
        MethodModel methodModel;
        Method method;
    }
}
