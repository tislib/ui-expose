package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import net.tislib.uiexpose.lib.data.UIExposeType;

public class TypeSerializer extends JsonSerializer<UIExposeType> {
    @Override
    public void serialize(UIExposeType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
