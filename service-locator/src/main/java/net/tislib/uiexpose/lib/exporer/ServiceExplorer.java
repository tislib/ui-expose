package net.tislib.uiexpose.lib.exporer;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class ServiceExplorer {
    private final String buildPath;

    public ServiceExplorer(String buildPath) {
        this.buildPath = buildPath;
    }

    @SneakyThrows
    public Set<Class<?>> findExposedServices() {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{
                new URL("file://" + buildPath)
        });

        Reflections reflections = new Reflections(
                "net.tislib",
                classLoader,
                new SubTypesScanner(false),
                new TypeAnnotationsScanner()
        );

        Set<Class<?>> exposedTypes = reflections.getTypesAnnotatedWith(UIExpose.class);
        return exposedTypes;
    }
}
