
package net.tislib.uiexpose.lib.processor;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.tislib.uiexpose.lib.data.UIExposeType;
import util.CommonUtil;


public class ExcludingTypeProcessor implements TypeProcessor {

    private final Predicate<String> excludeFilter;

    public ExcludingTypeProcessor(List<String> excludedTypes) {
        this(new Predicate<String>() {
            final Set<String> excludedTypesSet = excludedTypes != null ? new LinkedHashSet<>(excludedTypes) : Collections.emptySet();

            @Override
            public boolean test(String typeName) {
                return excludedTypesSet.contains(typeName);
            }
        });
    }

    public ExcludingTypeProcessor(Predicate<String> excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    @Override
    public Result processType(Type javaType, Context context) {
        final Class<?> rawClass = CommonUtil.getRawClassOrNull(javaType);
        if (rawClass != null && excludeFilter.test(rawClass.getName())) {
            return new Result(UIExposeType.UNKNOWN_TYPE);
        }
        return null;
    }

    @Override
    public UIExposeType<?> locateUiExposeType(Type javaType) {
        final Class<?> rawClass = CommonUtil.getRawClassOrNull(javaType);
        if (rawClass != null && excludeFilter.test(rawClass.getName())) {
            return UIExposeType.UNKNOWN_TYPE;
        }
        return null;
    }

}
