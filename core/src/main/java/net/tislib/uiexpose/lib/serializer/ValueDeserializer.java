package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import net.tislib.uiexpose.lib.data.Type;
import net.tislib.uiexpose.lib.data.Value;

public class ValueDeserializer extends JsonDeserializer<Value<?>> {

    @Override
    @SuppressWarnings("unchecked")
    public Value<?> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        Type<?> type = SerializationUtil.deserializeType(node.get("type"));
        return new Value(type, SerializationUtil.deserializeValue(type, node.get("value")));
    }
}
