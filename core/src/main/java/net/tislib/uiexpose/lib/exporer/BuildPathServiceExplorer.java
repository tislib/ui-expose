package net.tislib.uiexpose.lib.exporer;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import lombok.SneakyThrows;
import net.tislib.uiexpose.lib.annotations.UIExpose;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class BuildPathServiceExplorer implements ServiceExplorer {
    private final String buildPath;
    private Set<Class<?>> exposedServices;

    public BuildPathServiceExplorer(String buildPath) {
        if (!buildPath.endsWith("/")) {
            buildPath = buildPath + "/";
        }
        this.buildPath = buildPath;
    }

    @Override
    @SneakyThrows
    public void loadExposedServices() {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{
                new URL("file:" + buildPath)
        });

        Reflections reflections = new Reflections(
                "net.tislib",
                classLoader,
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
