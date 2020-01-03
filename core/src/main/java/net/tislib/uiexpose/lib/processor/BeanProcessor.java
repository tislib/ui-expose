package net.tislib.uiexpose.lib.processor;

import java.util.Set;
import net.tislib.uiexpose.lib.data.Model;

public interface BeanProcessor {
    void process(Model.ModelBuilder modelBuilder, Set<Class<?>> exposedTypes);

}
