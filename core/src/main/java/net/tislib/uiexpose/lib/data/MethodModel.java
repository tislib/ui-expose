package net.tislib.uiexpose.lib.data;

import java.util.Map;
import lombok.Data;

@Data
public class MethodModel {
    private String name;
    private UIExposeType<?> returnType;

    private Map<String, UIExposeType<?>> arguments;
}
