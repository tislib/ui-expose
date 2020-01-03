package util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import net.tislib.uiexpose.lib.data.Pair;

@UtilityClass
public class CommonUtil {

    public static Map<String, String> convertToMap(List<String> mappings) {
        final Map<String, String> result = new LinkedHashMap<>();
        if (mappings != null) {
            for (String mapping : mappings) {
                final String[] values = mapping.split(":", 2);
                if (values.length < 2) {
                    throw new RuntimeException("Invalid mapping format: " + mapping);
                }
                result.put(values[0].trim(), values[1].trim());
            }
        }
        return result;
    }

    public static <T> Class<? extends T> loadClass(ClassLoader classLoader, String className, Class<T> requiredClassType) {
        Objects.requireNonNull(classLoader, "classLoader");
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(requiredClassType, "requiredClassType");
        try {
            final Class<?> loadedClass = classLoader.loadClass(className);
            if (requiredClassType.isAssignableFrom(loadedClass)) {
                @SuppressWarnings("unchecked") final Class<? extends T> castedClass = (Class<? extends T>) loadedClass;
                return castedClass;
            } else {
                throw new RuntimeException(String.format("Class '%s' is not assignable to '%s'.", loadedClass, requiredClassType));
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getRawClassOrNull(Type type) {
        final Pair<Class<?>, List<Type>> rawClassAndTypeArguments = getRawClassAndTypeArguments(type);
        return rawClassAndTypeArguments != null ? rawClassAndTypeArguments.getValue1() : null;
    }

    public static Pair<Class<?>, List<Type>> getRawClassAndTypeArguments(Type type) {
        if (type instanceof Class) {
            final Class<?> javaClass = (Class<?>) type;
            return Pair.of(javaClass, Arrays.asList(javaClass.getTypeParameters()));
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                final Class<?> javaClass = (Class<?>) parameterizedType.getRawType();
                return Pair.of(javaClass, Arrays.asList(parameterizedType.getActualTypeArguments()));
            }
        }
        return null;
    }

    public static Type replaceRawClassInType(Type type, Class<?> newClass) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            return createParameterizedType(newClass, parameterizedType.getActualTypeArguments());
        }
        return newClass;
    }

    public static ParameterizedType createParameterizedType(final Type rawType, final List<Type> actualTypeArguments) {
        return createParameterizedType(rawType, actualTypeArguments.toArray(new Type[0]));
    }

    public static ParameterizedType createParameterizedType(final Type rawType, final Type... actualTypeArguments) {
        final Type ownerType = null;
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj instanceof ParameterizedType) {
                    final ParameterizedType that = (ParameterizedType) obj;
                    return
                            Objects.equals(ownerType, that.getOwnerType()) &&
                                    Objects.equals(rawType, that.getRawType()) &&
                                    Arrays.equals(actualTypeArguments, that.getActualTypeArguments());
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return Objects.hash(ownerType, rawType, actualTypeArguments);
            }

            @Override
            public String toString() {
                return (rawType instanceof Class ? ((Class<?>) rawType).getName() : rawType.getTypeName())
                        + "<"
                        + Stream.of(actualTypeArguments)
                        .map(type -> type.getTypeName())
                        .collect(Collectors.joining(", "))
                        + ">";
            }
        };
    }

    public static List<String> splitMultiline(String text, boolean trimOneLeadingSpaceOnLines) {
        if (text == null) {
            return null;
        }
        final List<String> result = new ArrayList<>();
        final String[] lines = text.split("\\r\\n|\\n|\\r");
        for (String line : lines) {
            result.add(trimOneLeadingSpaceOnLines ? trimOneLeadingSpaceOnly(line) : line);
        }
        return result;
    }

    private static String trimOneLeadingSpaceOnly(String line) {
        if (line.startsWith(" ")) {
            return line.substring(1);
        }
        return line;
    }

    public static <T> List<T> listFromNullable(T item) {
        return item != null ? Arrays.asList(item) : Collections.<T>emptyList();
    }

    public static <T> List<T> listFromNullable(List<T> list) {
        return list != null ? list : Collections.<T>emptyList();
    }

    public static <K, V> Map<K, V> mapFromNullable(Map<K, V> map) {
        return map != null ? map : Collections.<K, V>emptyMap();
    }

    public static <T> List<T> concat(List<? extends T> list1, List<? extends T> list2) {
        if (list1 == null && list2 == null) {
            return null;
        }
        final List<T> result = new ArrayList<>();
        if (list1 != null) result.addAll(list1);
        if (list2 != null) result.addAll(list2);
        return result;
    }
}
