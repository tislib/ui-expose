
package net.tislib.uiexpose.lib.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

//import cz.habarta.typescript.generator.TypeScriptGenerator;


/**
 * Name table.
 */
public class SymbolTable {

    private final LinkedHashMap<Pair<Class<?>, String>, Symbol> symbols = new LinkedHashMap<>();
    private final LinkedHashMap<String, Symbol> syntheticSymbols = new LinkedHashMap<>();
    private CustomTypeNamingFunction customTypeNamingFunction;

    public Symbol getSymbol(Class<?> cls) {
        return getSymbol(cls, null);
    }

    public Symbol getSymbol(Class<?> cls, String suffix) {
        final String suffixString = suffix != null ? suffix : "";
        final Pair<Class<?>, String> key = Pair.<Class<?>, String>of(cls, suffixString);
        if (!symbols.containsKey(key)) {
            symbols.put(key, new Symbol("$" + cls.getName().replace('.', '$') + suffixString + "$"));
        }
        return symbols.get(key);
    }

    public Symbol hasSymbol(Class<?> cls, String suffix) {
        return symbols.get(Pair.<Class<?>, String>of(cls, suffix));
    }

    public Class<?> getSymbolClass(Symbol symbol) {
        for (Map.Entry<Pair<Class<?>, String>, Symbol> entry : symbols.entrySet()) {
            if (entry.getValue() == symbol) {
                return entry.getKey().getValue1();
            }
        }
        return null;
    }

    public Symbol getSyntheticSymbol(String name) {
        if (!syntheticSymbols.containsKey(name)) {
            syntheticSymbols.put(name, new Symbol(name));
        }
        return syntheticSymbols.get(name);
    }

    public Symbol getSyntheticSymbol(String name, String suffix) {
        return getSyntheticSymbol(name + (suffix != null ? suffix : ""));
    }

    public Symbol addSuffixToSymbol(Symbol symbol, String suffix) {
        // try symbols
        for (Map.Entry<Pair<Class<?>, String>, Symbol> entry : symbols.entrySet()) {
            if (entry.getValue() == symbol) {
                return getSymbol(entry.getKey().getValue1(), entry.getKey().getValue2() + suffix);
            }
        }
        // syntheticSymbols
        return getSyntheticSymbol(symbol.getFullName() + suffix);
    }

    public void resolveSymbolNames() {
        final Map<String, List<Class<?>>> names = new LinkedHashMap<>();
        for (Map.Entry<Pair<Class<?>, String>, Symbol> entry : symbols.entrySet()) {
            final Class<?> cls = entry.getKey().getValue1();
            final String suffix = entry.getKey().getValue2();
            final Symbol symbol = entry.getValue();
            setSymbolQualifiedName(symbol, cls, suffix);
            final String fullName = symbol.getFullName();
            if (!names.containsKey(fullName)) {
                names.put(fullName, new ArrayList<Class<?>>());
            }
            names.get(fullName).add(cls);
        }
        reportConflicts(names);
    }

    private static void reportConflicts(Map<String, List<Class<?>>> names) {
        boolean conflict = false;
        for (Map.Entry<String, List<Class<?>>> entry : names.entrySet()) {
            final String name = entry.getKey();
            final List<Class<?>> classes = entry.getValue();
            if (classes.size() > 1) {
//                TypeScriptGenerator.getLogger().warning(String.format("Multiple classes are mapped to '%s' name. Conflicting classes: %s", name, classes));
                conflict = true;
            }
        }
        if (conflict) {
            throw new NameConflictException("Multiple classes are mapped to the same name. You can use 'customTypeNaming' or 'customTypeNamingFunction' settings to resolve conflicts or exclude conflicting class if it was added accidentally.");
        }
    }

    private void setSymbolQualifiedName(Symbol symbol, Class<?> cls, String suffix) {
        final String module;
        final String namespacedName;
        final String suffixString = suffix != null ? suffix : "";
    }

    // https://github.com/Microsoft/TypeScript/blob/master/doc/spec.md#221-reserved-words
    private static final Set<String> Keywords = new LinkedHashSet<>(Arrays.asList(
        "break",             "case",              "catch",             "class",
        "const",             "continue",          "debugger",          "default",
        "delete",            "do",                "else",              "enum",
        "export",            "extends",           "false",             "finally",
        "for",               "function",          "if",                "import",
        "in",                "instanceof",        "new",               "null",
        "return",            "super",             "switch",            "this",
        "throw",             "true",              "try",               "typeof",
        "var",               "void",              "while",             "with",

        "implements",        "interface",         "let",               "package",
        "private",           "protected",         "public",            "static",
        "yield"
    ));

    private static boolean isReservedWord(String word) {
        return Keywords.contains(word);
    }

    private static boolean isUndefined(Object variable) {
        return ScriptObjectMirror.isUndefined(variable);
    }

    private CustomTypeNamingFunction getCustomTypeNamingFunction() throws ScriptException {
        if (customTypeNamingFunction == null) {
            final String engineMimeType = "application/javascript";
            final ScriptEngineManager manager = new ScriptEngineManager();

            // getting ScriptEngine from manager doesn't work in Maven plugin on Java 9
//            final ScriptEngine engine = manager.getEngineByMimeType(engineMimeType);
            final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();

            if (engine == null) {
//                TypeScriptGenerator.getLogger().error(String.format("Script engine for '%s' MIME type not found. Available engines: %s", engineMimeType, manager.getEngineFactories().size()));
//                for (ScriptEngineFactory factory : manager.getEngineFactories()) {
//                    TypeScriptGenerator.getLogger().info(String.format("  %s %s - MIME types: %s", factory.getEngineName(), factory.getEngineVersion(), factory.getMimeTypes()));
//                }
                throw new RuntimeException("Cannot evaluate function specified using 'customTypeNamingFunction' parameter. See log for details.");
            }
            final Invocable invocable = (Invocable) engine;
            customTypeNamingFunction = invocable.getInterface(CustomTypeNamingFunction.class);
        }
        return customTypeNamingFunction;
    }

    /**
     * Function for getting custom TypeScript names for Java classes.
     * If the function returns null, the default is used.
     */
    public static interface CustomTypeNamingFunction {
        public Object getName(String className, String classSimpleName);
    }

    public boolean isImported(Symbol symbol) {
        final Class<?> cls = getSymbolClass(symbol);
        return false;
    }

    public static class NameConflictException extends RuntimeException {
        
        private static final long serialVersionUID = 1L;

        public NameConflictException() {
        }

        public NameConflictException(String message) {
            super(message);
        }

        public NameConflictException(String message, Throwable cause) {
            super(message, cause);
        }

        public NameConflictException(Throwable cause) {
            super(cause);
        }

    }

}