package net.tislib.uiexpose.lib.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.tislib.uiexpose.lib.data.BeanModel;
import net.tislib.uiexpose.lib.data.DeclarationModel;
import net.tislib.uiexpose.lib.data.EnumKind;
import net.tislib.uiexpose.lib.data.EnumMemberModel;
import net.tislib.uiexpose.lib.data.EnumModel;
import net.tislib.uiexpose.lib.data.Model;
import net.tislib.uiexpose.lib.data.PropertyMember;
import net.tislib.uiexpose.lib.data.PropertyModel;
import net.tislib.uiexpose.lib.data.UIExposeType;
import net.tislib.uiexpose.lib.serializer.JacksonSerializeModule;
import util.CommonUtil;
import util.GenericsResolver;

public class JacksonBeanProcessor implements BeanProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeProcessor commonTypeProcessor;
    final Queue<Class> typeQueue = new LinkedList<>();
    private final Jackson2Configuration config;

    public JacksonBeanProcessor(Jackson2Configuration config) {
        objectMapper.registerModule(new JacksonSerializeModule());

        this.commonTypeProcessor = createCommonTypeProcessor(Collections.singletonList(
                createSpecificTypeProcessor()
        ));

        this.config = config;
        if (config != null) {
            setVisibility(PropertyAccessor.FIELD, config.fieldVisibility);
            setVisibility(PropertyAccessor.GETTER, config.getterVisibility);
            setVisibility(PropertyAccessor.IS_GETTER, config.isGetterVisibility);
            setVisibility(PropertyAccessor.SETTER, config.setterVisibility);
            setVisibility(PropertyAccessor.CREATOR, config.creatorVisibility);
            if (config.shapeConfigOverrides != null) {
                config.shapeConfigOverrides.entrySet()
                        .forEach(entry -> setShapeOverride(entry.getKey(), entry.getValue()));
            }
            if (config.enumsUsingToString) {
                objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
                objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            }
        }
    }

    private void setVisibility(PropertyAccessor accessor, JsonAutoDetect.Visibility visibility) {
        if (visibility != null) {
            objectMapper.setVisibility(accessor, visibility);
        }
    }

    private void setShapeOverride(Class<?> cls, JsonFormat.Shape shape) {
        final MutableConfigOverride configOverride = objectMapper.configOverride(cls);
        configOverride.setFormat(
                JsonFormat.Value.merge(
                        configOverride.getFormat(),
                        JsonFormat.Value.forShape(shape)));
    }

    @Override
    public void process(Model.ModelBuilder modelBuilder, Set<Class<?>> exposedTypes) {
        typeQueue.clear();
        typeQueue.addAll(exposedTypes);
        final Collection<Class<?>> parsedTypes = new ArrayList<>();  // do not use hashcodes, we can only count on `equals` since we use custom `ParameterizedType`s
        final Set<BeanModel> beans = new HashSet<>();
        final Set<EnumModel> enums = new HashSet<>();
        Class<?> type;

        while ((type = typeQueue.poll()) != null) {
            if (parsedTypes.contains(type)) {
                continue;
            }
            parsedTypes.add(type);

            final TypeProcessor.Result result = commonTypeProcessor.processTypeInTemporaryContext(type, null);

            if (result.getUIExposeType().isObjectType()) {
                final DeclarationModel model = parseClass(type);
                if (model instanceof EnumModel) {
                    enums.add((EnumModel) model);
                } else if (model instanceof BeanModel) {
                    beans.add((BeanModel) model);
                } else {
                    throw new RuntimeException();
                }
            }

            typeQueue.addAll(result.getDiscoveredClasses());
        }

        modelBuilder.beans(beans);
        modelBuilder.enums(enums);
    }

    private DeclarationModel parseClass(Class<?> type) {
        final List<String> classComments = getComments(type.getAnnotation(JsonClassDescription.class));
        if (type.isEnum()) {
            return parseEnumOrObjectEnum(type, classComments);
        } else {
            return parseBean(type, classComments);
        }
    }

    private BeanModel parseBean(Class<?> type, List<String> classComments) {
        final List<PropertyModel> properties = new ArrayList<>();

        final BeanHelper beanHelper = getBeanHelper(type);
        if (beanHelper != null) {
            for (final BeanPropertyWriter beanPropertyWriter : beanHelper.getProperties()) {
                final Member member = beanPropertyWriter.getMember().getMember();
                final PropertyMember propertyMember = wrapMember(member, beanPropertyWriter.getName(), type);
                Type propertyType = propertyMember.getType();
                final List<String> propertyComments = getComments(beanPropertyWriter.getAnnotation(JsonPropertyDescription.class));

                final Jackson2TypeContext jackson2TypeContext = new Jackson2TypeContext(
                        this,
                        beanPropertyWriter,
                        config != null && config.disableObjectIdentityFeature);

                final boolean optional = !beanPropertyWriter.isRequired();
                // @JsonUnwrapped
                PropertyModel.PullProperties pullProperties = null;
                final JsonUnwrapped annotation = beanPropertyWriter.getAnnotation(JsonUnwrapped.class);
                if (annotation != null && annotation.enabled()) {
                    pullProperties = new PropertyModel.PullProperties(annotation.prefix(), annotation.suffix());
                }
                properties.add(processTypeAndCreateProperty(beanPropertyWriter.getName(), propertyType, jackson2TypeContext, optional, type, member, pullProperties, propertyComments));
            }
        }
        if (type.isEnum()) {
            return new BeanModel(type, null, null, null, null, null, properties, classComments, UIExposeType.objectType(type.getSimpleName()));
        }

        final String discriminantProperty;
        final String discriminantLiteral;

        final JsonTypeInfo jsonTypeInfo = type.getAnnotation(JsonTypeInfo.class);
        final JsonTypeInfo parentJsonTypeInfo;
        if (isSupported(jsonTypeInfo)) {
            // this is parent
            discriminantProperty = getDiscriminantPropertyName(jsonTypeInfo);
            discriminantLiteral = null;
        } else if (isSupported(parentJsonTypeInfo = getAnnotationRecursive(type, JsonTypeInfo.class))) {
            // this is child class
            discriminantProperty = getDiscriminantPropertyName(parentJsonTypeInfo);
            discriminantLiteral = getTypeName(parentJsonTypeInfo, type);
        } else {
            // not part of explicit hierarchy
            discriminantProperty = null;
            discriminantLiteral = null;
        }

        if (discriminantProperty != null && properties.stream().anyMatch(property -> Objects.equals(property.getName(), discriminantProperty))) {
//            TypeScriptGenerator.getLogger().warning(String.format(
//                    "Class '%s' has duplicate property '%s'. "
//                            + "For more information see 'https://github.com/vojtechhabarta/typescript-generator/issues/392'.",
//                    type.getName(), discriminantProperty));
        }

        final List<Class<?>> taggedUnionClasses;
        final JsonSubTypes jsonSubTypes = type.getAnnotation(JsonSubTypes.class);
        if (jsonSubTypes != null) {
            taggedUnionClasses = new ArrayList<>();
            for (JsonSubTypes.Type subType : jsonSubTypes.value()) {
                addBeanToQueue(subType.value());
                taggedUnionClasses.add(subType.value());
            }
        } else {
            taggedUnionClasses = null;
        }
        final Type superclass = type.getGenericSuperclass() == Object.class ? null : type.getGenericSuperclass();
        if (superclass != null) {
            addBeanToQueue((Class<?>) superclass);
        }
        final List<Type> interfaces = Arrays.asList(type.getGenericInterfaces());
        for (Type aInterface : interfaces) {
            if (aInterface instanceof Class) {
                addBeanToQueue((Class<?>) aInterface);
            }
        }
        return new BeanModel(type, superclass, taggedUnionClasses, discriminantProperty, discriminantLiteral, interfaces, properties, classComments, UIExposeType.objectType(type.getSimpleName()));
    }

    protected PropertyModel processTypeAndCreateProperty(String name, Type type, Object typeContext, boolean optional, Class<?> usedInClass, Member originalMember, PropertyModel.PullProperties pullProperties, List<String> comments) {
        final Type resolvedType = GenericsResolver.resolveType(usedInClass, type, originalMember.getDeclaringClass());
        final List<Class<?>> classes = commonTypeProcessor.discoverClassesUsedInType(resolvedType, typeContext);
        typeQueue.addAll(classes);
        return new PropertyModel(name, getUiExposeType(type), resolvedType, optional, originalMember, pullProperties, typeContext, comments);
    }

    private UIExposeType<?> getUiExposeType(Type type) {
        return commonTypeProcessor.locateUiExposeType(type);
    }


    private String getTypeName(JsonTypeInfo parentJsonTypeInfo, final Class<?> cls) {
        // Id.CLASS
        if (parentJsonTypeInfo.use() == JsonTypeInfo.Id.CLASS) {
            return cls.getName();
        }
        // find @JsonTypeName recursively
        final JsonTypeName jsonTypeName = getAnnotationRecursive(cls, JsonTypeName.class);
        if (jsonTypeName != null && !jsonTypeName.value().isEmpty()) {
            return jsonTypeName.value();
        }
        // find @JsonSubTypes.Type recursively
        final JsonSubTypes jsonSubTypes = getAnnotationRecursive(cls, JsonSubTypes.class, new Predicate<JsonSubTypes>() {
            @Override
            public boolean test(JsonSubTypes types) {
                return getJsonSubTypeForClass(types, cls) != null;
            }
        });
        if (jsonSubTypes != null) {
            final JsonSubTypes.Type jsonSubType = getJsonSubTypeForClass(jsonSubTypes, cls);
            if (!jsonSubType.name().isEmpty()) {
                return jsonSubType.name();
            }
        }
        // use simplified class name if it's not an interface or abstract
        if (!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())) {
            return cls.getName().substring(cls.getName().lastIndexOf(".") + 1);
        }
        return null;
    }

    private static JsonSubTypes.Type getJsonSubTypeForClass(JsonSubTypes types, Class<?> cls) {
        for (JsonSubTypes.Type type : types.value()) {
            if (type.value().equals(cls)) {
                return type;
            }
        }
        return null;
    }

    private static <T extends Annotation> T getAnnotationRecursive(Class<?> cls, Class<T> annotationClass) {
        return getAnnotationRecursive(cls, annotationClass, null);
    }

    private static <T extends Annotation> T getAnnotationRecursive(Class<?> cls, Class<T> annotationClass, Predicate<T> annotationFilter) {
        if (cls == null) {
            return null;
        }
        final T annotation = cls.getAnnotation(annotationClass);
        if (annotation != null && (annotationFilter == null || annotationFilter.test(annotation))) {
            return annotation;
        }
        for (Class<?> aInterface : cls.getInterfaces()) {
            final T interfaceAnnotation = getAnnotationRecursive(aInterface, annotationClass, annotationFilter);
            if (interfaceAnnotation != null) {
                return interfaceAnnotation;
            }
        }
        final T superclassAnnotation = getAnnotationRecursive(cls.getSuperclass(), annotationClass, annotationFilter);
        if (superclassAnnotation != null) {
            return superclassAnnotation;
        }
        return null;
    }


    protected void addBeanToQueue(Class<?> sourceType) {
        typeQueue.add(sourceType);
    }

    private String getDiscriminantPropertyName(JsonTypeInfo jsonTypeInfo) {
        return jsonTypeInfo.property().isEmpty()
                ? jsonTypeInfo.use().getDefaultPropertyName()
                : jsonTypeInfo.property();
    }

    private static boolean isSupported(JsonTypeInfo jsonTypeInfo) {
        return jsonTypeInfo != null &&
                jsonTypeInfo.include() == JsonTypeInfo.As.PROPERTY &&
                (jsonTypeInfo.use() == JsonTypeInfo.Id.NAME || jsonTypeInfo.use() == JsonTypeInfo.Id.CLASS);
    }

    private DeclarationModel parseEnumOrObjectEnum(Class<?> type, List<String> classComments) {
        final JsonFormat jsonFormat = type.getAnnotation(JsonFormat.class);
        if (jsonFormat != null && jsonFormat.shape() == JsonFormat.Shape.OBJECT) {
            return parseBean(type, classComments);
        }
        final boolean isNumberBased = jsonFormat != null && (
                jsonFormat.shape() == JsonFormat.Shape.NUMBER ||
                        jsonFormat.shape() == JsonFormat.Shape.NUMBER_FLOAT ||
                        jsonFormat.shape() == JsonFormat.Shape.NUMBER_INT);

        final List<EnumMemberModel> enumMembers = new ArrayList<>();
        if (type.isEnum()) {
            final Class<?> enumClass = (Class<?>) type;
            final Field[] allEnumFields = enumClass.getDeclaredFields();
            final List<Field> constants = Arrays.stream(allEnumFields).filter(Field::isEnumConstant).collect(Collectors.toList());
            for (Field constant : constants) {
                Object value;
                try {
                    constant.setAccessible(true);
                    final String enumJson = objectMapper.writeValueAsString(constant.get(null));
                    value = objectMapper.readValue(enumJson, new TypeReference<Object>() {
                    });
                } catch (Throwable e) {
//                    TypeScriptGenerator.getLogger().error(String.format("Cannot get enum value for constant '%s.%s'", enumClass.getName(), constant.getName()));
//                    TypeScriptGenerator.getLogger().verbose(Utils.exceptionToString(e));
                    e.printStackTrace();
                    value = constant.getName();
                }

                final List<String> constantComments = getComments(constant.getAnnotation(JsonPropertyDescription.class));
                if (value instanceof String) {
                    enumMembers.add(new EnumMemberModel(constant.getName(), (String) value, constantComments));
                } else if (value instanceof Number) {
                    enumMembers.add(new EnumMemberModel(constant.getName(), (Number) value, constantComments));
                } else {
//                    TypeScriptGenerator.getLogger().warning(String.format("'%s' enum as a @JsonValue that isn't a String or Number, ignoring", enumClass.getName()));
                }
            }
        }

        return new EnumModel(type, isNumberBased ? EnumKind.NumberBased : EnumKind.StringBased, enumMembers, classComments, UIExposeType.enumType(type.getSimpleName()));
    }

    private static List<String> getComments(JsonClassDescription classDescriptionAnnotation) {
        final String propertyDescriptionValue = classDescriptionAnnotation != null ? classDescriptionAnnotation.value() : null;
        final List<String> classComments = CommonUtil.splitMultiline(propertyDescriptionValue, false);
        return classComments;
    }

    private static List<String> getComments(JsonPropertyDescription propertyDescriptionAnnotation) {
        final String propertyDescriptionValue = propertyDescriptionAnnotation != null ? propertyDescriptionAnnotation.value() : null;
        final List<String> propertyComments = CommonUtil.splitMultiline(propertyDescriptionValue, false);
        return propertyComments;
    }


    private static TypeProcessor createCommonTypeProcessor(List<TypeProcessor> specificTypeProcessors) {
        final List<TypeProcessor> processors = new ArrayList<>();
        processors.add(new ExcludingTypeProcessor(Collections.emptyList()));
        processors.addAll(specificTypeProcessors);
        processors.add(new DefaultTypeProcessor());
        final TypeProcessor typeProcessor = new TypeProcessor.Chain(processors);
        return typeProcessor;
    }

    private static TypeProcessor createSpecificTypeProcessor() {
        return new TypeProcessor.Chain(
                new ExcludingTypeProcessor(Arrays.asList(JsonNode.class.getName())),
                new TypeProcessor() {
                    @Override
                    public TypeProcessor.Result processType(Type javaType, TypeProcessor.Context context) {
                        if (context.getTypeContext() instanceof Jackson2TypeContext) {
                            final Jackson2TypeContext jackson2TypeContext = (Jackson2TypeContext) context.getTypeContext();
                            if (!jackson2TypeContext.disableObjectIdentityFeature) {
                                final Type resultType = jackson2TypeContext.parser.processIdentity(javaType, jackson2TypeContext.beanPropertyWriter);
                                if (resultType != null) {
                                    return context.withTypeContext(null).processType(resultType);
                                }
                            }
                            // Map.Entry
                            final Class<?> rawClass = CommonUtil.getRawClassOrNull(javaType);
                            if (rawClass != null && Map.Entry.class.isAssignableFrom(rawClass)) {
                                final ObjectMapper objectMapper = jackson2TypeContext.parser.objectMapper;
                                final SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
                                final BeanDescription beanDescription = serializationConfig
                                        .introspect(TypeFactory.defaultInstance().constructType(rawClass));
                                final JsonFormat.Value formatOverride = serializationConfig.getDefaultPropertyFormat(Map.Entry.class);
                                final JsonFormat.Value formatFromAnnotation = beanDescription.findExpectedFormat(null);
                                final JsonFormat.Value format = JsonFormat.Value.merge(formatFromAnnotation, formatOverride);
                                if (format.getShape() != JsonFormat.Shape.OBJECT) {
                                    final Type mapType = CommonUtil.replaceRawClassInType(javaType, Map.class);
                                    return context.processType(mapType);
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public UIExposeType<?> locateUiExposeType(Type type) {
                        return new DefaultTypeProcessor().locateUiExposeType(type);
                    }
                }
        );
    }

    private static class Jackson2TypeContext {
        public final JacksonBeanProcessor parser;
        public final BeanPropertyWriter beanPropertyWriter;
        public final boolean disableObjectIdentityFeature;

        public Jackson2TypeContext(JacksonBeanProcessor parser, BeanPropertyWriter beanPropertyWriter, boolean disableObjectIdentityFeature) {
            this.parser = parser;
            this.beanPropertyWriter = beanPropertyWriter;
            this.disableObjectIdentityFeature = disableObjectIdentityFeature;
        }
    }

    protected static PropertyMember wrapMember(Member propertyMember, String propertyName, Class<?> sourceClass) {
        if (propertyMember instanceof Field) {
            final Field field = (Field) propertyMember;
            return new PropertyMember.FieldPropertyMember(field);
        }
        if (propertyMember instanceof Method) {
            final Method method = (Method) propertyMember;
            return new PropertyMember.MethodPropertyMember(method);
        }
        throw new RuntimeException(String.format(
                "Unexpected member type '%s' in property '%s' in class '%s'",
                propertyMember != null ? propertyMember.getClass().getName() : null,
                propertyName,
                sourceClass.getName()));
    }

    // @JsonIdentityInfo and @JsonIdentityReference
    private Type processIdentity(Type propertyType, BeanPropertyWriter propertyWriter) {

        final Class<?> clsT = CommonUtil.getRawClassOrNull(propertyType);
        final Class<?> clsW = propertyWriter.getType().getRawClass();
        final Class<?> cls = clsT != null ? clsT : clsW;

        if (cls != null) {
            final JsonIdentityInfo identityInfoC = cls.getAnnotation(JsonIdentityInfo.class);
            final JsonIdentityInfo identityInfoP = propertyWriter.getAnnotation(JsonIdentityInfo.class);
            final JsonIdentityInfo identityInfo = identityInfoP != null ? identityInfoP : identityInfoC;
            if (identityInfo == null) {
                return null;
            }
            final JsonIdentityReference identityReferenceC = cls.getAnnotation(JsonIdentityReference.class);
            final JsonIdentityReference identityReferenceP = propertyWriter.getAnnotation(JsonIdentityReference.class);
            final JsonIdentityReference identityReference = identityReferenceP != null ? identityReferenceP : identityReferenceC;
            final boolean alwaysAsId = identityReference != null && identityReference.alwaysAsId();

            final Type idType;
            if (identityInfo.generator() == ObjectIdGenerators.None.class) {
                return null;
            } else if (identityInfo.generator() == ObjectIdGenerators.PropertyGenerator.class) {
                final BeanHelper beanHelper = getBeanHelper(cls);
                if (beanHelper == null) {
                    return null;
                }
                final BeanPropertyWriter[] properties = beanHelper.getProperties();
                final Optional<BeanPropertyWriter> idProperty = Stream.of(properties)
                        .filter(p -> p.getName().equals(identityInfo.property()))
                        .findFirst();
                if (idProperty.isPresent()) {
                    final BeanPropertyWriter idPropertyWriter = idProperty.get();
                    final Member idMember = idPropertyWriter.getMember().getMember();
                    final PropertyMember idPropertyMember = wrapMember(idMember, idPropertyWriter.getName(), cls);
                    idType = idPropertyMember.getType();
                } else {
                    return null;
                }
            } else if (identityInfo.generator() == ObjectIdGenerators.IntSequenceGenerator.class) {
                idType = Integer.class;
            } else if (identityInfo.generator() == ObjectIdGenerators.UUIDGenerator.class) {
                idType = String.class;
            } else if (identityInfo.generator() == ObjectIdGenerators.StringIdGenerator.class) {
                idType = String.class;
            } else {
                idType = Object.class;
            }
            return alwaysAsId
                    ? idType
                    : idType; //new UnionType(propertyType, idType);
        }
        return null;
    }

    private BeanHelper getBeanHelper(Class<?> beanClass) {
        if (beanClass == null) {
            return null;
        }
        if (beanClass == Enum.class) {
            return null;
        }
        try {
            final DefaultSerializerProvider.Impl serializerProvider1 = (DefaultSerializerProvider.Impl) objectMapper.getSerializerProvider();
            final DefaultSerializerProvider.Impl serializerProvider2 = serializerProvider1.createInstance(objectMapper.getSerializationConfig(), objectMapper.getSerializerFactory());
            final JavaType simpleType = objectMapper.constructType(beanClass);
            final JsonSerializer<?> jsonSerializer = BeanSerializerFactory.instance.createSerializer(serializerProvider2, simpleType);
            if (jsonSerializer == null) {
                return null;
            }
            if (jsonSerializer instanceof BeanSerializer) {
                return new BeanHelper((BeanSerializer) jsonSerializer);
            } else {
                return null;
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class BeanHelper extends BeanSerializer {
        private static final long serialVersionUID = 1;

        public BeanHelper(BeanSerializer src) {
            super(src);
        }

        public BeanPropertyWriter[] getProperties() {
            return _props;
        }

    }
}
