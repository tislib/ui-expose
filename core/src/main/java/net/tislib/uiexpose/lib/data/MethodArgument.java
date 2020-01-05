package net.tislib.uiexpose.lib.data;

import lombok.Data;

@Data
public class MethodArgument {
    private String name;
    private UIExposeType<?> type;

    public static MethodArgument create(String name, UIExposeType<?> resolveType) {
        MethodArgument methodArgument = new MethodArgument();
        methodArgument.setName(name);
        methodArgument.setType(resolveType);
        return methodArgument;
    }
}
