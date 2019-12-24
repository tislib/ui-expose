package net.tislib.uiexpose.lib.data;

public final class Type<T> {

    private final String typeName;
    private final String typeRef;

    public static Type<Integer> INTEGER_TYPE = new Type<>("integer");
    public static Type<String> STRING_TYPE = new Type<>("string");
    public static Type<Float> FLOAT_TYPE = new Type<>("float");
    public static Type<Double> DOUBLE_TYPE = new Type<>("double");
    public static Type<Boolean> BOOLEAN_TYPE = new Type<>("boolean");

    private Type(String typeName, String typeRef) {
        this.typeName = typeName;
        this.typeRef = typeRef;
    }

    private Type(String typeName) {
        this.typeName = typeName;
        this.typeRef = null;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
