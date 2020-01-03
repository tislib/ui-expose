package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import net.tislib.uiexpose.lib.data.Value;

public class ValueSerializer extends JsonSerializer<Value> {
    @Override
    public void serialize(Value value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }
}
