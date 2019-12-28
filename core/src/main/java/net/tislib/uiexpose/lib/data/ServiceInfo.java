package net.tislib.uiexpose.lib.data;

import java.util.Set;
import lombok.Data;

@Data
public class ServiceInfo {
    private String name;
    private String group;

    private Set<MethodInfo> methods;
}
