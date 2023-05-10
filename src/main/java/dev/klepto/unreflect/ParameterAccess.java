package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Named;
import dev.klepto.unreflect.property.Reflectable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Represents a parameter of declared constructor or method of a class. Enables annotation, type and name access.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface ParameterAccess extends Reflectable, Named {

    /**
     * Reflection representation of the parameter.
     *
     * @return a reflection representation of the parameter
     * @see Constructor#getParameters()
     * @see Method#getParameters()
     */
    Parameter source();

    /**
     * Returns the constructor that contains this parameter.
     *
     * @return the constructor containing this parameter
     * @throws RuntimeException if parameter belongs to a method and not a constructor
     */
    default ConstructorAccess<?> constructor() throws RuntimeException {
        return (ConstructorAccess<?>) parent();
    }

    /**
     * Returns the method that contains this parameter.
     *
     * @return the method containing this parameter
     * @throws RuntimeException if parameter belongs to a constructor and not a method
     */
    default MethodAccess method() throws RuntimeException {
        return (MethodAccess) parent();
    }

}
