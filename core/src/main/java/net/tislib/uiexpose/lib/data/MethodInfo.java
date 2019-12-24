package net.tislib.uiexpose.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class MethodInfo {
    private String name;
    private Type returnType;

    @JsonIgnore
    private Method method;

    private List<Type> argumentTypes;
}
