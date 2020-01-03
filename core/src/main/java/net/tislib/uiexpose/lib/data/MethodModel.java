package net.tislib.uiexpose.lib.data;

import java.util.List;
import lombok.Data;

@Data
public class MethodModel {
    private String name;
    private UIExposeType<?> returnType;

    private List<MethodArgument> arguments;
}
