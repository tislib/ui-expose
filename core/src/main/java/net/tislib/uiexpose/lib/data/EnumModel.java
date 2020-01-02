
package net.tislib.uiexpose.lib.data;

import java.util.List;


public class EnumModel extends DeclarationModel {

    private final EnumKind kind;
    private final List<EnumMemberModel> members;

    public EnumModel(Class<?> origin, EnumKind kind, List<EnumMemberModel> members, List<String> comments, UIExposeType<?> type) {
        super (origin, comments, type);
        this.kind = kind;
        this.members = members;
    }

    public EnumKind getKind() {
        return kind;
    }

    public List<EnumMemberModel> getMembers() {
        return members;
    }

    public EnumModel withMembers(List<EnumMemberModel> members) {
        return new EnumModel(origin, kind, members, comments, getType());
    }

    @Override
    public EnumModel withComments(List<String> comments) {
        return new EnumModel(origin, kind, members, comments, getType());
    }

}
