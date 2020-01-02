
package cz.habarta.typescript.generator;


public class TsParameter {

    public final String name;
    public final UIExposePrimitiveType tsType;

    public TsParameter(String name, UIExposePrimitiveType tsType) {
        this.name = name;
        this.tsType = tsType;
    }

    public String getName() {
        return name;
    }

    public UIExposePrimitiveType getTsType() {
        return tsType;
    }

}
