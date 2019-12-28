package net.tislib.uiexpose.lib.exporer;

import java.util.Set;
import lombok.SneakyThrows;

public interface ServiceExplorer {

    @SneakyThrows
    void loadExposedServices();

    Set<Class<?>> getExposedServices();
}
