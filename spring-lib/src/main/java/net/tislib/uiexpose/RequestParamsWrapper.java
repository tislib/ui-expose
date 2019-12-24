package net.tislib.uiexpose;

import java.util.List;
import lombok.Data;
import net.tislib.uiexpose.lib.data.Value;

@Data
public class RequestParamsWrapper {

    private List<Value<?>> values;

}
