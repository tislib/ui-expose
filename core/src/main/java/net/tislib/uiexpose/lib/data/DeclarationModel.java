
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

    public DeclarationModel(Class<?> origin, List<String> comments) {
        this.origin = origin;
        this.comments = comments;
        this.name = origin.getSimpleName();
    }

    public abstract DeclarationModel withComments(List<String> comments);

}
