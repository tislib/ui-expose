
package net.tislib.uiexpose.lib.processor;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import javax.xml.bind.JAXBElement;
import net.tislib.uiexpose.lib.data.UIExposeType;


public class DefaultTypeProcessor implements TypeProcessor {

    @Override
    public Result processType(Type javaType, Context context) {
        if (KnownTypes.containsKey(javaType)) {
            return new Result(KnownTypes.get(javaType));
        }
        if (javaType instanceof Class) {
            final Class<?> javaClass = (Class<?>) javaType;
            if (Temporal.class.isAssignableFrom(javaClass)) {
                return new Result(UIExposeType.DATE_TYPE);
            }
        }
        if (javaType instanceof Class) {
            final Class<?> javaClass = (Class<?>) javaType;
            if (javaClass.isArray()) {
//                final Result result = context.processType(javaClass.getComponentType());
//                return new Result(new UIExposeType.BasicArrayType(result.getUIExposeType()), result.getDiscoveredClasses());
            }
            if (javaClass.isEnum()) {
//                return new Result(new UIExposeType.EnumReferenceType(context.getSymbol(javaClass)), javaClass);
            }
            if (Collection.class.isAssignableFrom(javaClass)) {
//                return new Result(new UIExposeType.BasicArrayType(UIExposeType.Any));
            }
            if (Map.class.isAssignableFrom(javaClass)) {
//                return new Result(new UIExposeType.IndexedArrayType(UIExposeType.String, UIExposeType.Any));
            }
            if (OptionalInt.class.isAssignableFrom(javaClass) ||
                    OptionalLong.class.isAssignableFrom(javaClass) ||
                    OptionalDouble.class.isAssignableFrom(javaClass)) {
//                return new Result(UIExposeType.Number.optional());
            }
            if (JAXBElement.class.isAssignableFrom(javaClass)) {
//                return new Result(UIExposeType.Any);
            }
            // generic structural type used without type arguments
            if (javaClass.getTypeParameters().length > 0) {
                final List<UIExposeType> tsTypeArguments = new ArrayList<>();
                for (int i = 0; i < javaClass.getTypeParameters().length; i++) {
//                    tsTypeArguments.add(UIExposeType.Any);
                }
//                return new Result(new UIExposeType.GenericReferenceType(context.getSymbol(javaClass), tsTypeArguments));
            }
            // structural type
            return new Result(UIExposeType.objectType(javaClass.getSimpleName()), javaClass);
        }
        if (javaType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) javaType;
            if (parameterizedType.getRawType() instanceof Class) {
                final Class<?> javaClass = (Class<?>) parameterizedType.getRawType();
                if (Collection.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
//                    return new Result(new UIExposeType.BasicArrayType(result.getUIExposeType()), result.getDiscoveredClasses());
                }
                if (Map.class.isAssignableFrom(javaClass)) {
                    final Result keyResult = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    final Result valueResult = context.processType(parameterizedType.getActualTypeArguments()[1]);
                    if (keyResult.getUIExposeType().isEnumType()) {
//                        return new Result(
//                                new UIExposeType.MappedType(keyResult.getUIExposeType(), UIExposeType.MappedType.QuestionToken.Question, valueResult.getUIExposeType()),
//                                CommonUtil.concat(keyResult.getDiscoveredClasses(), valueResult.getDiscoveredClasses())
//                        );
                    } else {
//                        return new Result(
//                                new UIExposeType.IndexedArrayType(UIExposeType.String, valueResult.getUIExposeType()),
//                                valueResult.getDiscoveredClasses()
//                        );
                    }
                }
                if (Optional.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
//                    return new Result(result.getUIExposeType().optional(), result.getDiscoveredClasses());
                }
                if (JAXBElement.class.isAssignableFrom(javaClass)) {
                    final Result result = context.processType(parameterizedType.getActualTypeArguments()[0]);
                    return new Result(result.getUIExposeType(), result.getDiscoveredClasses());
                }
                // generic structural type
                final List<Class<?>> discoveredClasses = new ArrayList<>();
                discoveredClasses.add(javaClass);
                final List<UIExposeType> tsTypeArguments = new ArrayList<>();
                for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                    final TypeProcessor.Result typeArgumentResult = context.processType(typeArgument);
                    tsTypeArguments.add(typeArgumentResult.getUIExposeType());
                    discoveredClasses.addAll(typeArgumentResult.getDiscoveredClasses());
                }
//                return new Result(new UIExposeType.GenericReferenceType(context.getSymbol(javaClass), tsTypeArguments), discoveredClasses);
            }
        }
        if (javaType instanceof GenericArrayType) {
            final GenericArrayType genericArrayType = (GenericArrayType) javaType;
            final Result result = context.processType(genericArrayType.getGenericComponentType());
//            return new Result(new UIExposeType.BasicArrayType(result.getUIExposeType()), result.getDiscoveredClasses());
        }
        if (javaType instanceof TypeVariable) {
            final TypeVariable<?> typeVariable = (TypeVariable<?>) javaType;
            if (typeVariable.getGenericDeclaration() instanceof Method) {
                // example method: public <T extends Number> T getData();
                return context.processType(typeVariable.getBounds()[0]);
            }
//            return new Result(new UIExposeType.GenericVariableType(typeVariable.getName()));
        }
        if (javaType instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) javaType;
            final Type[] upperBounds = wildcardType.getUpperBounds();
//            return upperBounds.length > 0
//                    ? context.processType(upperBounds[0])
//                    : new Result(UIExposeType.Any);
        }
//        if (javaType instanceof UnionType) {
//            final UnionType unionType = (UnionType) javaType;
//            final List<Result> results = unionType.types.stream()
//                    .map(type -> context.processType(type))
//                    .collect(Collectors.toList());
//            return new Result(
//                    new UIExposeType.UnionType(results.stream()
//                            .map(result -> result.getUIExposeType())
//                            .collect(Collectors.toList())),
//                    results.stream()
//                            .flatMap(result -> result.getDiscoveredClasses().stream())
//                            .collect(Collectors.toList())
//            );
//        }
        return new Result(UIExposeType.UNKNOWN_TYPE);
    }

    public UIExposeType<?> locateUiExposeType(Type javaType) {
        if (KnownTypes.containsKey(javaType)) {
            return KnownTypes.get(javaType);
        } else {
            if(javaType instanceof Class) {
               return  UIExposeType.objectType(((Class<?>) javaType).getSimpleName());
            } else {
                return UIExposeType.objectType(javaType.getTypeName());
            }
        }
    }

    private static Map<Type, UIExposeType> getKnownTypes() {
        final Map<Type, UIExposeType> knownTypes = new LinkedHashMap<>();
        // java.lang
//        knownTypes.put(Object.class, UIExposeType.Any);
//        knownTypes.put(Byte.class, UIExposeType.Number);
//        knownTypes.put(Byte.TYPE, UIExposeType.Number);
//        knownTypes.put(Short.class, UIExposeType.Number);
//        knownTypes.put(Short.TYPE, UIExposeType.Number);
        knownTypes.put(Integer.class, UIExposeType.INTEGER_TYPE);
        knownTypes.put(Integer.TYPE, UIExposeType.INTEGER_TYPE);
//        knownTypes.put(Long.class, UIExposeType.Number);
//        knownTypes.put(Long.TYPE, UIExposeType.Number);
//        knownTypes.put(Float.class, UIExposeType.Number);
//        knownTypes.put(Float.TYPE, UIExposeType.Number);
//        knownTypes.put(Double.class, UIExposeType.Number);
//        knownTypes.put(Double.TYPE, UIExposeType.Number);
//        knownTypes.put(Boolean.class, UIExposeType.Boolean);
//        knownTypes.put(Boolean.TYPE, UIExposeType.Boolean);
        knownTypes.put(Character.class, UIExposeType.STRING_TYPE);
        knownTypes.put(Character.TYPE, UIExposeType.STRING_TYPE);
        knownTypes.put(String.class, UIExposeType.STRING_TYPE);
//        knownTypes.put(void.class, UIExposeType.Void);
//        knownTypes.put(Void.class, UIExposeType.Void);
//        knownTypes.put(Number.class, UIExposeType.Number);
        // other java packages
//        knownTypes.put(BigDecimal.class, UIExposeType.Number);
//        knownTypes.put(BigInteger.class, UIExposeType.Number);
//        knownTypes.put(Date.class, UIExposeType.Date);
//        knownTypes.put(UUID.class, UIExposeType.String);
        return knownTypes;
    }

    private static final Map<Type, UIExposeType> KnownTypes = getKnownTypes();

}
