
package net.tislib.uiexpose.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Getter;


@Getter
public abstract class DeclarationModel {
    private String name;

    @JsonIgnore
    protected final Class<?> origin;
    protected final List<String> comments;
    protected final UIExposeType<?> type;

    public DeclarationModel(Class<?> origin, List<String> comments, UIExposeType<?> type) {
        this.origin = origin;
        this.comments = comments;
        this.type = type;
        this.name = origin.getSimpleName();
    }

    public abstract DeclarationModel withComments(List<String> comments);

    public UIExposeType<?> getType() {
        return type;
    }
}
