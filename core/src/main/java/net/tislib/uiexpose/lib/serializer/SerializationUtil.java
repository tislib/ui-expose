package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.experimental.UtilityClass;
import net.tislib.uiexpose.lib.data.Type;

@UtilityClass
public class SerializationUtil {

    public Type<?> deserializeType(JsonNode node) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            switch (textNode.asText()) {
                case "string":
                    return Type.STRING_TYPE;
            }
        }
        return null;
    }

    public static Object deserializeValue(Type<?> type, JsonNode value) {
        if (type == Type.STRING_TYPE) {
            return value.asText();
        }
        return null;
    }
}
