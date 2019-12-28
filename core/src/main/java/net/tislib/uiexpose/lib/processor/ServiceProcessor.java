package net.tislib.uiexpose.lib.processor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.tislib.uiexpose.lib.data.ServiceInfo;

public interface ServiceProcessor {
    Set<ServiceInfo> process(Set<Class<?>> exposedServices);
    Map<Class<?>, ServiceInfo> processMapped(Set<Class<?>> exposedServices);
}
