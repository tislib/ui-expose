package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.experimental.UtilityClass;
import net.tislib.uiexpose.lib.data.UIExposeType;

@UtilityClass
public class SerializationUtil {

    public UIExposeType<?> deserializeType(JsonNode node) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            switch (textNode.asText()) {
                case "string":
                    return UIExposeType.STRING_TYPE;
            }
        }
        return null;
    }

    public static Object deserializeValue(UIExposeType<?> type, JsonNode value) {
        if (type == UIExposeType.STRING_TYPE) {
            return value.asText();
        }
        return null;
    }
}
