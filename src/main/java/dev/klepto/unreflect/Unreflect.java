package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.reflection.ReflectionClassAccess;
import dev.klepto.unreflect.util.JdkInternals;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Unreflect is small but very powerful alternative to java reflection API. Provides easy to use type, class, field,
 * method and constructor lookup and access. Enables high-performance reflection by using code generation. Bypasses all
 * JVM security checks to enable access to any modules and members for both reflection based and bytecode generated API.
 * Reflection API is accessible by {@link Unreflect#reflect(Class)}.
 * Code-generated API is accessible by {@link Unreflect#unreflect(Class)}.
 * Library utilizes JDK's internal API and is not guaranteed to work with different versions of JDK, this library should
 * not be used in production.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 * @see <a href="https://github.com/klepto/unreflect">GitHub Repository</a>
 */
public class Unreflect {

    /**
     * Creates {@link ClassAccess} for a given object which generates bytecode for accessing its members. This method
     * automatically calls {@link Accessible#bind(Object)} on created accessor in order to skip on supplying instance
     * every time you want to access its members.
     *
     * @param targetObject the target object
     * @param <T>          type that represents the class of the object
     * @return a class accessor that will use code-generation rather than reflection for accessing members
     * @see Unreflect#unreflect(Class)
     */
    public static <T> ClassAccess<T> unreflect(T targetObject) {
        return reflect(targetObject).unreflect();
    }

    /**
     * Creates {@link ClassAccess} for a given class which generates bytecode for accessing its members. Access to any
     * fields, methods or constructors will automatically generate code for direct access to all members. Bytecode
     * generation is very costly and should be only used during initialization in order to get performance benefits
     * it provides over regular reflection. If you want bytecode to be only generated for specific members, use
     * {@link Unreflect#reflect(Object)} for lookup and then call {@link Accessible#unreflect()}.
     *
     * @param targetClass the target class
     * @param <T>         type that represents the class
     * @return a class accessor that will use code-generation rather than reflection for accessing its members
     */
    public static <T> ClassAccess<T> unreflect(Class<T> targetClass) {
        return reflect(targetClass).unreflect();
    }

    /**
     * Creates {@link ClassAccess} for a given object that use reflection API to access its members. This method
     * automatically calls {@link Accessible#bind(Object)} on created accessor in order to skip on supplying instance
     * every time you want to access its members.
     *
     * @param targetObject the target object
     * @param <T>          type that represents the class of the object
     * @return a class accessor that will use code-generation rather than reflection for accessing members
     * @see Unreflect#reflect(Class)
     */
    public static <T> ClassAccess<T> reflect(T targetObject) {
        val targetClass = (Class<T>) targetObject.getClass();
        return reflect(targetClass).bind(targetObject);
    }

    /**
     * Creates {@link ClassAccess} for a given class name that use reflection API to access its members.
     *
     * @param targetClassName the target class name
     * @return a class accessor that will use reflection API for accessing its members
     * @throws RuntimeException if class with a given name cannot be found
     * @see Unreflect#reflect(Class)
     */
    @SneakyThrows
    public static ClassAccess<?> reflect(String targetClassName) throws RuntimeException {
        return reflect(Class.forName(targetClassName));
    }

    /**
     * Creates {@link ClassAccess} for a given class that use reflection API to access its members. All members are
     * automatically set to accessible upon lookup to prevent any security checks by the JVM. This allows for
     * unrestricted access to anything regardless of module or visibility.
     *
     * @param targetClass the target class
     * @param <T>         type that represents the class
     * @return a class accessor that will use reflection API for accessing its members
     */
    public static <T> ClassAccess<T> reflect(Class<T> targetClass) {
        return new ReflectionClassAccess<>(targetClass, null);
    }

}
