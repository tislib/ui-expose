package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import net.tislib.uiexpose.lib.data.Type;
import net.tislib.uiexpose.lib.data.Value;

public class JacksonSerializeModule extends SimpleModule {

    public JacksonSerializeModule() {
        this.addSerializer(Type.class, new TypeSerializer());
        this.addDeserializer(Type.class, new TypeDeserializer());
        this.addSerializer(Value.class, new ValueSerializer());
        this.addDeserializer(Value.class, new ValueDeserializer());
    }
}
