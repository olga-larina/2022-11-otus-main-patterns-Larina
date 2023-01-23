package ru.otus.spacebattle.adapter;

import org.joor.Reflect;
import ru.otus.spacebattle.domain.UObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdapterGeneratorImpl implements AdapterGenerator {

    private static final String NAME_TEMPLATE = "ru.otus.spacebattle.adapter.%sAdapter";
    private static final String CONTENT_TEMPLATE = "" +
        "package ru.otus.spacebattle.adapter; \n" +
        "import ru.otus.spacebattle.ioc.IoC;\n" +
        "import ru.otus.spacebattle.domain.UObject;\n" +
        "import %2$s;\n" +
        "public class %1$sAdapter implements %1$s {\n" +
        "   private UObject obj;\n" +
        "   public %1$sAdapter(UObject obj) {\n" +
        "       this.obj = obj;\n" +
        "   }\n";
    private static final String GETTER_TEMPLATE = "" +
        "   public %1$s get%2$s() {\n" +
        "       return (%1$s) IoC.resolve(\"%3$s:%4$s.get\", obj);\n" +
        "   }\n";
    private static final String SETTER_TEMPLATE = "" +
        "   public void set%2$s(%1$s newValue) {\n" +
        "       IoC.resolve(\"%3$s:%4$s.set\", obj, newValue);\n" +
        "   }\n";
    private static final Pattern GETTER_PATTERN = Pattern.compile("get(.*)");
    private static final Pattern SETTER_PATTERN = Pattern.compile("set(.*)");

    private final Map<String, Reflect> adapters = new ConcurrentHashMap<>();

    @Override
    public <T> T resolve(Class<T> interfaceType, UObject uObject) {
        try {
            Reflect adapter = adapters.get(interfaceType.getName());
            if (adapter == null) {
                adapter = generate(interfaceType);
            }
            return adapter.create(uObject).get(); // getClass().getConstructor(uObject.getClass()).newInstance(uObject)
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Reflect generate(Class<?> interfaceType) {
        try {
            String name = String.format(NAME_TEMPLATE, interfaceType.getSimpleName());
            String content = String.format(CONTENT_TEMPLATE, interfaceType.getSimpleName(), interfaceType.getName());
            StringBuilder builder = new StringBuilder(content);
            for (Method method : interfaceType.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    Matcher getterMatcher = GETTER_PATTERN.matcher(method.getName());
                    Matcher setterMatcher = SETTER_PATTERN.matcher(method.getName());
                    String methodCode = null;
                    if (getterMatcher.matches() && !method.getReturnType().equals(Void.TYPE)) {
                        methodCode = generateGetter(method.getReturnType(), getterMatcher.group(1), interfaceType);
                    } else if (setterMatcher.matches() && method.getParameterCount() > 0) {
                        methodCode = generateSetter(method.getParameters()[0].getType(), setterMatcher.group(1), interfaceType);
                    }
                    if (methodCode != null) {
                        builder.append(methodCode);
                    }
                }
            }
            builder.append("}\n");
//            System.out.println("Adapter generated: " + builder);
            Reflect adapter = Reflect.compile(name, builder.toString());
            adapters.put(interfaceType.getName(), adapter);
            return adapter;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private String generateGetter(Class<?> returnType, String attribute, Class<?> interfaceType) {
        return String.format(GETTER_TEMPLATE, returnType.getName(), attribute, interfaceType.getSimpleName(), attribute.toLowerCase());
    }

    private String generateSetter(Class<?> parameterType, String attribute, Class<?> interfaceType) {
        return String.format(SETTER_TEMPLATE, parameterType.getName(), attribute, interfaceType.getSimpleName(), attribute.toLowerCase());
    }

}
