package net.tislib.uiexpose.lib.data;

import java.util.Set;
import lombok.Data;

@Data
public class ServiceModel {
    private String name;
    private String group;

    private Set<MethodModel> methods;
}
