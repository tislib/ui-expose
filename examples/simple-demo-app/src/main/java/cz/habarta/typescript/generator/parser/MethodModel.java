
package cz.habarta.typescript.generator.parser;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;


public class MethodModel {

    protected final Class<?> originClass;
    protected final String name;
    protected final List<MethodParameterModel> parameters;
    protected final Type returnType;
    protected final List<String> comments;

    public MethodModel(Class<?> originClass, String name, List<MethodParameterModel> parameters, Type returnType, List<String> comments) {
        this.originClass = originClass;
        this.name = name;
        this.parameters = parameters != null ? parameters : Collections.<MethodParameterModel>emptyList();
        this.returnType = returnType;
        this.comments = comments;
    }

    public Class<?> getOriginClass() {
        return originClass;
    }

    public String getName() {
        return name;
    }

    public List<MethodParameterModel> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<String> getComments() {
        return comments;
    }

    public MethodModel withComments(List<String> comments) {
        return new MethodModel(originClass, name, parameters, returnType, comments);
    }

}
