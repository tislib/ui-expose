
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.compiler.Symbol;
import cz.habarta.typescript.generator.compiler.SymbolTable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public interface TypeProcessor {

    /**
     * @return <code>null</code> if this processor didn't process passed java type
     */
    public Result processType(Type javaType, Context context);

    public default Result processTypeInTemporaryContext(Type type, Object typeContext, Settings settings) {
        return processType(type, new Context(new SymbolTable(settings), this, typeContext));
    }

    public default List<Class<?>> discoverClassesUsedInType(Type type, Object typeContext, Settings settings) {
        final TypeProcessor.Result result = processTypeInTemporaryContext(type, typeContext, settings);
        return result != null ? result.getDiscoveredClasses() : Collections.emptyList();
    }

    public static class Context {

        private final SymbolTable symbolTable;
        private final TypeProcessor typeProcessor;
        private final Object typeContext;

        public Context(SymbolTable symbolTable, TypeProcessor typeProcessor, Object typeContext) {
            this.symbolTable = Objects.requireNonNull(symbolTable, "symbolTable");
            this.typeProcessor = Objects.requireNonNull(typeProcessor, "typeProcessor");
            this.typeContext = typeContext;
        }

        public Symbol getSymbol(Class<?> cls) {
            return symbolTable.getSymbol(cls);
        }

        public Result processType(Type javaType) {
            return typeProcessor.processType(javaType, this);
        }

        public Object getTypeContext() {
            return typeContext;
        }

        public Context withTypeContext(Object typeContext) {
            return new Context(symbolTable, typeProcessor, typeContext);
        }

    }

    public static class Result {

//        private final UIExposePrimitiveType tsType;
        private boolean isReferenceType;
        private final List<Class<?>> discoveredClasses;


        public Result(List<Class<?>> discoveredClasses) {
            this.discoveredClasses = discoveredClasses;
        }

        public Result(Class<?>... discoveredClasses) {
            this.discoveredClasses = Arrays.asList(discoveredClasses);
        }

        public List<Class<?>> getDiscoveredClasses() {
            return discoveredClasses;
        }

        public boolean isReferenceType() {
            return isReferenceType;
        }

        public void setReferenceType(boolean referenceType) {
            isReferenceType = referenceType;
        }
    }

    public static class Chain implements TypeProcessor {

        private final List<TypeProcessor> processors;

        public Chain(List<TypeProcessor> processors) {
            this.processors = processors;
        }

        public Chain(TypeProcessor... processors) {
            this.processors = Arrays.asList(processors);
        }

        @Override
        public Result processType(Type javaType, Context context) {
            for (TypeProcessor processor : processors) {
                final Result result = processor.processType(javaType, context);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

    }

}
