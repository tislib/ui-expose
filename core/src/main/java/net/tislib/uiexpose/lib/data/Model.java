
package net.tislib.uiexpose.lib.data;

import java.util.Objects;
import java.util.Set;
import lombok.Builder;

@Builder
public class Model {

    private final Set<BeanModel> beans;
    private final Set<EnumModel> enums;
    private final Set<ServiceModel> services;

    public Model(Set<BeanModel> beans, Set<EnumModel> enums, Set<ServiceModel> services) {
        this.beans = Objects.requireNonNull(beans);
        this.enums = Objects.requireNonNull(enums);
        this.services = services;
    }

    public Set<BeanModel> getBeans() {
        return beans;
    }

    public BeanModel getBean(Class<?> beanClass) {
        for (BeanModel bean : beans) {
            if (bean.getOrigin().equals(beanClass)) {
                return bean;
            }
        }
        return null;
    }

    public Set<EnumModel> getEnums() {
        return enums;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Model{");
        sb.append(String.format("%n"));
        for (BeanModel bean : beans) {
            sb.append("  ");
            sb.append(bean);
            sb.append(String.format("%n"));
        }
        for (EnumModel enumModel : enums) {
            sb.append("  ");
            sb.append(enumModel);
            sb.append(String.format("%n"));
        }
        sb.append('}');
        return sb.toString();
    }

    public Set<ServiceModel> getServices() {
        return services;
    }
}
