
package net.tislib.uiexpose.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class BeanModel extends DeclarationModel {

    @JsonIgnore
    private final Type parent;

    @JsonIgnore
    private final List<Class<?>> taggedUnionClasses;
    private final String discriminantProperty;
    private final String discriminantLiteral;

    @JsonIgnore
    private final List<Type> interfaces;
    private final List<PropertyModel> properties;

    public BeanModel(Class<?> origin, Type parent, List<Class<?>> taggedUnionClasses, String discriminantProperty, String discriminantLiteral, List<Type> interfaces, List<PropertyModel> properties, List<String> comments, UIExposeType<?> type) {
        super(origin, comments, type);
        this.parent = parent;
        this.taggedUnionClasses = taggedUnionClasses;
        this.discriminantProperty = discriminantProperty;
        this.discriminantLiteral = discriminantLiteral;
        this.interfaces = interfaces == null ? Collections.emptyList() : interfaces;
        this.properties = properties;
    }

    @Override
    public BeanModel withComments(List<String> comments) {
        return new BeanModel(origin, parent, taggedUnionClasses, discriminantProperty, discriminantLiteral, interfaces, properties, comments, getType());
    }

    @Override
    public String toString() {
        return "BeanModel{" + "origin=" + getOrigin() + ", properties=" + properties + '}';
    }

}
