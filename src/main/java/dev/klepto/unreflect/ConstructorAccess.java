package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.property.Invokable;
import dev.klepto.unreflect.property.Reflectable;

import java.lang.reflect.Constructor;

/**
 * Represents a declared constructor of a class. Enables creation of new instances. Contains invocation, annotation,
 * modifiers and parameter access.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface ConstructorAccess<T> extends Reflectable, Invokable, Accessible<ConstructorAccess<T>> {

    /**
     * Reflection representation of the constructor.
     *
     * @return a reflection representation of the constructor
     * @see Class#getDeclaredConstructors()
     */
    Constructor<T> source();

    /**
     * Invokes constructor with given argument values and returns the new instance of the class.
     *
     * @param args the constructor arguments
     * @return a new instance of the class
     * @throws RuntimeException if any exception is thrown during initialization of the class or if constructor doesn't
     *                          accept given arguments
     */
    T create(Object... args) throws RuntimeException;

}
