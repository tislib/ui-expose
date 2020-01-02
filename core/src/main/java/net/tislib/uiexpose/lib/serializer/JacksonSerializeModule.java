package net.tislib.uiexpose.lib.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import net.tislib.uiexpose.lib.data.UIExposeType;
import net.tislib.uiexpose.lib.data.Value;

public class JacksonSerializeModule extends SimpleModule {

    public JacksonSerializeModule() {
        this.addSerializer(UIExposeType.class, new TypeSerializer());
        this.addDeserializer(UIExposeType.class, new TypeDeserializer());
        this.addSerializer(Value.class, new ValueSerializer());
        this.addDeserializer(Value.class, new ValueDeserializer());
    }
}
