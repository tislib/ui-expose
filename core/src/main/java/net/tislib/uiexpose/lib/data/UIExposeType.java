package net.tislib.uiexpose.lib.data;

public final class UIExposeType<T> {

    private static final String ENUM = "enum";
    private static final String OBJECT = "object";

    public static UIExposeType<Integer> INTEGER_TYPE = new UIExposeType<>("integer");
    public static UIExposeType<String> STRING_TYPE = new UIExposeType<>("string");
    public static UIExposeType<Float> FLOAT_TYPE = new UIExposeType<>("float");
    public static UIExposeType<Double> DOUBLE_TYPE = new UIExposeType<>("double");
    public static UIExposeType<Boolean> BOOLEAN_TYPE = new UIExposeType<>("boolean");
    public static final UIExposeType<Void> UNKNOWN_TYPE = new UIExposeType<>("unknown");
    public static final UIExposeType<Void> DATE_TYPE = new UIExposeType<>("date");

    private final String typeName;
    private final String typeRef;

    private UIExposeType(String typeName, String typeRef) {
        this.typeName = typeName;
        this.typeRef = typeRef;
    }

    private UIExposeType(String typeName) {
        this.typeName = typeName;
        this.typeRef = null;
    }

    public static UIExposeType<Enum<?>> enumType(String simpleName) {
        return new UIExposeType<>(ENUM, simpleName);
    }

    public static UIExposeType<?> objectType(String simpleName) {
        return new UIExposeType<>(OBJECT, simpleName);
    }

    @Override
    public String toString() {
        if (typeRef == null) {
            return typeName;
        } else {
            return "#" + typeRef;
        }
    }

    public boolean isEnumType() {
        return typeName.equals(ENUM);
    }

    public boolean isObjectType() {
        return typeName.equals(OBJECT);
    }
}
