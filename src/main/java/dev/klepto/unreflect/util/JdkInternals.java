package dev.klepto.unreflect.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * A set of very unsafe JDK utility methods. The heavy usage of internal JDK API means that this not guaranteed to work
 * on all versions of JDK and might break with future versions. Use with caution.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class JdkInternals {

    private static final Unsafe unsafe;
    private static final int accessibleFlagIndex;
    @Getter private static final Class<?> magicAccessorImpl;
    @Getter private static final Class<?> classDefiner;
    private static final Method defineClassMethod;

    static {
        unsafe = getUnsafe();
        accessibleFlagIndex = findAccessibleFlagIndex();
        magicAccessorImpl = findMagicAccessorImpl();
        classDefiner = findClassDefiner();
        defineClassMethod = getDefineClassMethod();
    }

    private JdkInternals() {
    }

    @SneakyThrows
    private static int findAccessibleFlagIndex() {
        val methodA = JdkInternals.class.getDeclaredMethods()[0];
        val methodB = JdkInternals.class.getDeclaredMethods()[0];
        methodA.setAccessible(true);
        methodB.setAccessible(false);

        for (int index = 0; index < Byte.MAX_VALUE; index++) {
            val valueA = unsafe.getBoolean(methodA, (long) index);
            val valueB = unsafe.getBoolean(methodB, (long) index);
            if (valueA != valueB) {
                return index;
            }
        }

        throw new RuntimeException("Couldn't detect accessible flag index.");
    }

    @SneakyThrows
    private static Unsafe getUnsafe() {
        try {
            val theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            return (Unsafe) theUnsafeField.get(null);
        } catch (Throwable err) {
            throw new RuntimeException("Couldn't acquire the Unsafe in current JDK version.");
        }
    }

    private static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> findClassDefiner() {
        Class<?> classDefiner = getClass("jdk.internal.reflect.ClassDefiner");
        if (classDefiner == null) {
            classDefiner = getClass("sun.reflect.ClassDefiner");
        }
        if (classDefiner == null) {
            throw new RuntimeException("Couldn't detect ClassDefiner in current JDK version.");
        }
        return classDefiner;
    }

    private static Method getDefineClassMethod() {
        try {
            val method = classDefiner.getDeclaredMethods()[0];
            setAccessible(method, true);
            return method;
        } catch (Throwable err) {
            throw new RuntimeException("Couldn't detect defineClass method in current JDK version.");
        }
    }

    private static Class<?> findMagicAccessorImpl() {
        Class<?> magicAccessorImpl = getClass("jdk.internal.reflect.MagicAccessorImpl");
        if (magicAccessorImpl == null) {
            magicAccessorImpl = getClass("sun.reflect.MagicAccessorImpl");
        }
        if (magicAccessorImpl == null) {
            throw new RuntimeException("Couldn't detect MagicAccessorImpl in current JDK version.");
        }
        return magicAccessorImpl;
    }

    /**
     * Checks if reflection object is accessible ({@link AccessibleObject#setAccessible(boolean)}) using Unsafe API.
     * Since accessible flag is private property, this accesses memory directly to acquire the boolean.
     *
     * @param object the reflection object
     * @return true if object is flagged as accessible
     */
    public static boolean isAccessible(AccessibleObject object) {
        return unsafe.getBoolean(object, accessibleFlagIndex);
    }

    /**
     * Changes reflection object's accessible flag bypassing all security checks using Unsafe API. This is achieved by
     * changing field value directly in the memory. Note that altering memory directly is significantly faster than
     * using field reflection.
     *
     * @param object     the reflection object
     * @param accessible the accessible flag
     */
    public static void setAccessible(AccessibleObject object, boolean accessible) {
        unsafe.putBoolean(object, accessibleFlagIndex, accessible);
    }

    /**
     * Allocates a new instance of a {@link java.lang.Class} without invoking any constructors.
     *
     * @param type the class type
     * @return the instantiated object
     */
    @SneakyThrows
    public static Object allocateInstance(Class<?> type) {
        return unsafe.allocateInstance(type);
    }

    /**
     * Defines a class in the target {@link ClassLoader}. Utilizes the class loading of native {@link java.lang.reflect}
     * API. Using JDK's internal class loader bypasses a lot of security checks and allows to load bytecode that otherwise
     * be impossible to load by a regular class loader.
     *
     * @param classLoader the target class loader
     * @param name        the name of the class
     * @param bytecode    the class bytecode
     * @return the loaded class
     */
    @SneakyThrows
    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytecode) {
        return (Class<?>) defineClassMethod.invoke(
                null,
                name,
                bytecode,
                0,
                bytecode.length,
                classLoader
        );
    }

}
