package net.tislib.uiexpose.lib.exporer;

import java.util.Set;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class LocalServiceExplorer implements ServiceExplorer {
    private Set<Class<?>> exposedServices;

    @Override
    public void loadExposedServices() {
        Reflections reflections = new Reflections(
                new SubTypesScanner(false),
                new TypeAnnotationsScanner()
        );

        this.exposedServices = reflections.getTypesAnnotatedWith(UIExpose.class);
    }

    @Override
    public Set<Class<?>> getExposedServices() {
        return exposedServices;
    }
}
