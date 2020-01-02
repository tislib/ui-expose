
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.parser.Jackson2Parser;
import cz.habarta.typescript.generator.parser.ModelParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeScriptGenerator {

    private static Logger logger = new Logger();

    private final Settings settings;

    public TypeScriptGenerator(Settings settings) {
        this.settings = settings;
        settings.validate();
    }

    public static Logger getLogger() {
        return logger;
    }

    private TypeProcessor createTypeProcessor(List<TypeProcessor> specificTypeProcessors) {
        final List<TypeProcessor> processors = new ArrayList<>();
        processors.add(new ExcludingTypeProcessor(settings.getExcludeFilter()));
        if (settings.customTypeProcessor != null) {
            processors.add(settings.customTypeProcessor);
        }
        processors.add(new CustomMappingTypeProcessor(settings.getValidatedCustomTypeMappings()));
        processors.addAll(specificTypeProcessors);
        processors.add(new DefaultTypeProcessor());
        final TypeProcessor typeProcessor = new TypeProcessor.Chain(processors);
        return typeProcessor;
    }

    public ModelParser getModelParser() {
        Jackson2Parser.Jackson2ParserFactory parserFactory = new Jackson2Parser.Jackson2ParserFactory();

        return parserFactory.create(settings, createTypeProcessor(
                Collections.singletonList(parserFactory.getSpecificTypeProcessor())
        ), Collections.emptyList());

    }


}
