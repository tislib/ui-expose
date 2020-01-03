
package net.tislib.uiexpose.lib.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import util.CommonUtil;


public class Jackson2Configuration {

    public JsonAutoDetect.Visibility fieldVisibility;
    public JsonAutoDetect.Visibility getterVisibility;
    public JsonAutoDetect.Visibility isGetterVisibility;
    public JsonAutoDetect.Visibility setterVisibility;
    public JsonAutoDetect.Visibility creatorVisibility;
    public Map<Class<?>, JsonFormat.Shape> shapeConfigOverrides;
    public boolean enumsUsingToString;
    public boolean disableObjectIdentityFeature;

//    public static Jackson2Configuration from(Jackson2Configuration configuration, ClassLoader classLoader) {
//        final Jackson2Configuration resolved = new Jackson2Configuration();
//        resolved.fieldVisibility = configuration.fieldVisibility;
//        resolved.getterVisibility = configuration.getterVisibility;
//        resolved.isGetterVisibility = configuration.isGetterVisibility;
//        resolved.setterVisibility = configuration.setterVisibility;
//        resolved.creatorVisibility = configuration.creatorVisibility;
//        resolved.fieldVisibility = configuration.fieldVisibility;
//        resolved.shapeConfigOverrides = resolveShapeConfigOverrides(configuration.shapeConfigOverrides, classLoader);
//        resolved.enumsUsingToString = configuration.enumsUsingToString;
//        resolved.disableObjectIdentityFeature = configuration.disableObjectIdentityFeature;
//        return resolved;
//    }

    private static Map<Class<?>, JsonFormat.Shape> resolveShapeConfigOverrides(List<String> overrides, ClassLoader classLoader) {
        if (overrides == null) {
            return null;
        }
        final Map<Class<?>, JsonFormat.Shape> resolvedOverrides = new LinkedHashMap<>();
        final Map<String, String> overridesMap = CommonUtil.convertToMap(overrides);
        for (Map.Entry<String, String> entry : overridesMap.entrySet()) {
            final Class<?> cls = CommonUtil.loadClass(classLoader, entry.getKey(), Object.class);
            final JsonFormat.Shape shape = JsonFormat.Shape.valueOf(entry.getValue());
            resolvedOverrides.put(cls, shape);
        }
        return resolvedOverrides;
    }

    public void setVisibility(
            JsonAutoDetect.Visibility fieldVisibility,
            JsonAutoDetect.Visibility getterVisibility,
            JsonAutoDetect.Visibility isGetterVisibility,
            JsonAutoDetect.Visibility setterVisibility,
            JsonAutoDetect.Visibility creatorVisibility) {
        this.fieldVisibility = fieldVisibility;
        this.getterVisibility = getterVisibility;
        this.isGetterVisibility = isGetterVisibility;
        this.setterVisibility = setterVisibility;
        this.creatorVisibility = creatorVisibility;
    }

}
