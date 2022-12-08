import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SecondTest {

    public static final String DATA_FORMAT = "dd.MM.yyyy HH:mm";
    public static  <T>T loadFromProperties(Class<T> cls, Path propertiesPath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATA_FORMAT);
        Properties properties = getProperties((propertiesPath));

        try {
            T newObject = cls.cast(cls.getDeclaredConstructor().newInstance());
            Method[] methods = newObject.getClass().getMethods();
            Map<Class<?>, Method> setterMap = Arrays.stream(methods)
                    .filter(method -> method.getName().startsWith("set"))
                    .collect(Collectors.toMap(method -> method.getParameterTypes()[0],
                            Function.identity()));
            Field[] fields = newObject.getClass().getFields();
            Map<Class<?>, Property> collect = Arrays.stream(fields)
                    .filter(field -> field.getAnnotation(Property.class) != null)
                    .collect(Collectors.toMap(Field::getType,
                            field -> field.getAnnotation(Property.class)));

            String stringProperty = properties.getProperty(collect.getOrDefault(String.class,
                    getDefaultProperty("stringProperty", "")).name());
            setterMap.get(stringProperty.getClass()).invoke(newObject, stringProperty);

            Integer numberProperty = Integer.valueOf(properties.getProperty(collect.getOrDefault(Integer.class,
                    getDefaultProperty("numberProperty", "")).name()));
            setterMap.get(numberProperty.getClass()).invoke(newObject, numberProperty);

            LocalDateTime timeProperty = LocalDateTime.parse((properties.getProperty(collect.getOrDefault(LocalDateTime.class,
                    getDefaultProperty("timeProperty", "")).name())), formatter);
            setterMap.get(timeProperty.getClass()).invoke(newObject, timeProperty);

            return cls.cast(newObject);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("This method is no found in this Class", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Property getDefaultProperty(String name, String format) {
        return new Property() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Property.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String format() {
                return format;
            }
        };
    }

    private static Properties getProperties(Path propertiesPath) {
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(propertiesPath.toFile());
            properties.load(fileInputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
