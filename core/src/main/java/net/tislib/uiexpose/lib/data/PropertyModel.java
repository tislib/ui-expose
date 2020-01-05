
package net.tislib.uiexpose.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class PropertyModel {

    private final String name;
    private final UIExposeType<?> type;
    @JsonIgnore
    private final Type propertyType;
    private final boolean optional;
    @JsonIgnore
    private final Member originalMember;
    @JsonIgnore
    private final PullProperties pullProperties;
    @JsonIgnore
    private final Object context;
    @JsonIgnore
    private final List<String> comments;

    public static class PullProperties {
        public final String prefix;
        public final String suffix;

        public PullProperties(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }

    public PropertyModel(String name, UIExposeType<?> type, Type propertyType, boolean optional, Member originalMember, PullProperties pullProperties, Object context, List<String> comments) {
        this.name = Objects.requireNonNull(name);
        this.type = type;
        this.propertyType = Objects.requireNonNull(propertyType);
        this.optional = optional;
        this.originalMember = originalMember;
        this.pullProperties = pullProperties;
        this.context = context;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "PropertyModel{" + "name=" + name + ", type=" + propertyType + "}";
    }

}
