package net.tislib.uiexpose.lib.processor;

import java.util.Map;
import java.util.Set;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.data.ServiceModel;

public interface ServiceProcessor {
    Model process(Set<Class<?>> exposedServices);
    Map<Class<?>, ServiceProcessorImpl.ServiceInfo> processMapped(Set<Class<?>> exposedServices);
}
