
package cz.habarta.typescript.generator;

//import cz.habarta.typescript.generator.emitter.Emitter;


public class TsProperty {

    public final String name;
    public final UIExposePrimitiveType tsType;

    public TsProperty(String name, UIExposePrimitiveType tsType) {
        this.name = name;
        this.tsType = tsType;
    }

    public String getName() {
        return name;
    }

    public UIExposePrimitiveType getTsType() {
        return tsType;
    }

//    public String format(Settings settings) {
//        final String questionMark = (tsType instanceof UIExposePrimitiveType.OptionalType) ? "?" : "";
//        return Emitter.quoteIfNeeded(name, settings) + questionMark + ": " + tsType.format(settings) + ";";
//    }

}
