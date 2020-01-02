
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.util.UnionType;
import cz.habarta.typescript.generator.util.Utils;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;


public class DefaultTypeProcessor implements TypeProcessor {

    @Override
    public Result processType(Type javaType, Context context) {
        if (KnownTypes.containsKey(javaType)) {
            return new Result();
        }
        if (javaType instanceof Class) {
            final Class<?> javaClass = (Class<?>) javaType;
            if (Temporal.class.isAssignableFrom(javaClass)) {
                return new Result();
            }
        }
        if (javaType instanceof Class) {
            final Class<?> javaClass = (Class<?>) javaType;
            if (javaClass.isArray()) {
                final Result result = context.processType(javaClass.getComponentType());
                return new Result(result.getDiscoveredClasses());
            }
            if (javaClass.isEnum()) {
                return new Result(javaClass);
            }
            if (Collection.class.isAssignableFrom(javaClass)) {
                return new Result();
            }
            if (Map.class.isAssignableFrom(javaClass)) {
                return new Result();
            }
            if (OptionalInt.class.isAssignableFrom(javaClass) ||
                    OptionalLong.class.isAssignableFrom(javaClass) ||
                    OptionalDouble.class.isAssignableFrom(javaClass)) {
                return new Result();
            }
            if (JAXBElement.class.isAssignableFrom(javaClass)) {
                return new Result();
            }
            // generic structural type used without type arguments
            if (javaClass.getTypeParameters().length > 0) {
                final List<UIExposePrimitiveType> tsTypeArguments = new ArrayList<>();
                for (int i = 0; i < javaClass.getTypeParameters().length; i++) {
                    tsTypeArguments.add(UIExposePrimitiveType.Any);
                }
                return new Result();
            }
            // structural type
            return new Result(javaClass);
        }
        if (javaType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) javaType;
            if (parameterizedType.getRawType() instanceof Class) {
                final Class<?> javaClass = (Class<?>) parameterizedType.getRawType();
                if (Collection.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    return new Result(result.getDiscoveredClasses());
                }
                if (Map.class.isAssignableFrom(javaClass)) {
                    final Result keyResult = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    final Result valueResult = context.processType(parameterizedType.getActualTypeArguments()[1]);
//                    if (keyResult.getTsType() instanceof UIExposePrimitiveType.EnumReferenceType) {
                    if (true) {
                        return new Result(
                                Utils.concat(keyResult.getDiscoveredClasses(), valueResult.getDiscoveredClasses())
                        );
                    } else {
                        return new Result(
                                valueResult.getDiscoveredClasses()
                        );
                    }
                }
                if (Optional.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    return new Result(result.getDiscoveredClasses());
                }
                if (JAXBElement.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    return new Result(result.getDiscoveredClasses());
                }
                // generic structural type
                final List<Class<?>> discoveredClasses = new ArrayList<>();
                discoveredClasses.add(javaClass);
                final List<UIExposePrimitiveType> tsTypeArguments = new ArrayList<>();
                for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                    final TypeProcessor.Result typeArgumentResult = context.processType(typeArgument);
                    discoveredClasses.addAll(typeArgumentResult.getDiscoveredClasses());
                }
                return new Result(discoveredClasses);
            }
        }
        if (javaType instanceof GenericArrayType) {
            final GenericArrayType genericArrayType = (GenericArrayType) javaType;
            final Result result = context.processType(genericArrayType.getGenericComponentType());
            return new Result(result.getDiscoveredClasses());
        }
        if (javaType instanceof TypeVariable) {
            final TypeVariable<?> typeVariable = (TypeVariable<?>) javaType;
            if (typeVariable.getGenericDeclaration() instanceof Method) {
                // example method: public <T extends Number> T getData();
                return context.processType(typeVariable.getBounds()[0]);
            }
            return new Result();
        }
        if (javaType instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) javaType;
            final Type[] upperBounds = wildcardType.getUpperBounds();
            return upperBounds.length > 0
                    ? context.processType(upperBounds[0])
                    : new Result();
        }
        if (javaType instanceof UnionType) {
            final UnionType unionType = (UnionType) javaType;
            final List<Result> results = unionType.types.stream()
                    .map(type -> context.processType(type))
                    .collect(Collectors.toList());
            return new Result(
                    results.stream()
                            .flatMap(result -> result.getDiscoveredClasses().stream())
                            .collect(Collectors.toList())
            );
        }
        return null;
    }

    private static Map<Type, UIExposePrimitiveType> getKnownTypes() {
        final Map<Type, UIExposePrimitiveType> knownTypes = new LinkedHashMap<>();
        // java.lang
        knownTypes.put(Object.class, UIExposePrimitiveType.Any);
        knownTypes.put(Byte.class, UIExposePrimitiveType.Number);
        knownTypes.put(Byte.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Short.class, UIExposePrimitiveType.Number);
        knownTypes.put(Short.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Integer.class, UIExposePrimitiveType.Number);
        knownTypes.put(Integer.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Long.class, UIExposePrimitiveType.Number);
        knownTypes.put(Long.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Float.class, UIExposePrimitiveType.Number);
        knownTypes.put(Float.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Double.class, UIExposePrimitiveType.Number);
        knownTypes.put(Double.TYPE, UIExposePrimitiveType.Number);
        knownTypes.put(Boolean.class, UIExposePrimitiveType.Boolean);
        knownTypes.put(Boolean.TYPE, UIExposePrimitiveType.Boolean);
        knownTypes.put(Character.class, UIExposePrimitiveType.String);
        knownTypes.put(Character.TYPE, UIExposePrimitiveType.String);
        knownTypes.put(String.class, UIExposePrimitiveType.String);
        knownTypes.put(void.class, UIExposePrimitiveType.Void);
        knownTypes.put(Void.class, UIExposePrimitiveType.Void);
        knownTypes.put(Number.class, UIExposePrimitiveType.Number);
        // other java packages
        knownTypes.put(BigDecimal.class, UIExposePrimitiveType.Number);
        knownTypes.put(BigInteger.class, UIExposePrimitiveType.Number);
        knownTypes.put(Date.class, UIExposePrimitiveType.Date);
        knownTypes.put(UUID.class, UIExposePrimitiveType.String);
        return knownTypes;
    }

    private static final Map<Type, UIExposePrimitiveType> KnownTypes = getKnownTypes();

}
