package net.tislib.uiexpose.lib.data;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class MethodInfo {
    private String name;
    private Type returnType;

    private Set<Type> argumentTypes;
}
