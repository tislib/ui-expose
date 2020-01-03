package net.tislib.uiexpose.demo1;

import cz.habarta.typescript.generator.Input;
import cz.habarta.typescript.generator.JsonLibrary;
import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.TypeScriptGenerator2;
import cz.habarta.typescript.generator.TypeScriptOutputKind;
import cz.habarta.typescript.generator.parser.Model;

public class Test1 {

    public static void main(String... args) {
        Settings settings = new Settings();
        settings.outputKind = TypeScriptOutputKind.module;
        settings.jsonLibrary = JsonLibrary.jackson2;

        TypeScriptGenerator2 typeScriptGenerator = new TypeScriptGenerator2(settings);

        Input input = Input.from(
                Person.class
        );

        final Model model = typeScriptGenerator.getModelParser().parseModel(input.getSourceTypes());

        System.out.println(model);

    }

}
