
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.compiler.Symbol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UIExposePrimitiveType {

    public static final UIExposePrimitiveType Any = new BasicType("any");
    public static final UIExposePrimitiveType Boolean = new BasicType("boolean");
    public static final UIExposePrimitiveType Number = new BasicType("number");
    public static final UIExposePrimitiveType String = new BasicType("string");
    public static final UIExposePrimitiveType Date = new BasicType("Date");
    public static final UIExposePrimitiveType Void = new BasicType("void");
//    public static final UIExposePrimitiveType Undefined = new BasicType("undefined");
//    public static final UIExposePrimitiveType Null = new BasicType("null");
//    public static final UIExposePrimitiveType Never = new BasicType("never");
//    public static final UIExposePrimitiveType Unknown = new BasicType("unknown");

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && this.getClass() == rhs.getClass() && this.toString().equals(rhs.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public static class BasicType extends UIExposePrimitiveType {

        public final String name;

        public BasicType(String name) {
            this.name = name;
        }

    }

    public static class ReferenceType extends UIExposePrimitiveType {

        public final Symbol symbol;

        public ReferenceType(Symbol symbol) {
            this.symbol = symbol;
        }

    }

    public static class VerbatimType extends UIExposePrimitiveType {

        public final String verbatimType;

        public VerbatimType(String verbatimType) {
            this.verbatimType = verbatimType;
        }

    }

    public static class GenericBasicType extends UIExposePrimitiveType.BasicType {

        public final List<UIExposePrimitiveType> typeArguments;

        public GenericBasicType(String name, UIExposePrimitiveType... typeArguments) {
            this(name, Arrays.asList(typeArguments));
        }

        public GenericBasicType(String name, List<? extends UIExposePrimitiveType> typeArguments) {
            super(name);
            this.typeArguments = new ArrayList<>(typeArguments);
        }

    }
}
