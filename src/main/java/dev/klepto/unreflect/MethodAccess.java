package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.property.Invokable;
import dev.klepto.unreflect.property.Named;
import dev.klepto.unreflect.property.Reflectable;

import java.lang.reflect.Method;

/**
 * Represents a declared method of a class. Enables method invocation. Contains name, invocation, type, annotation,
 * modifiers and parameter access.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface MethodAccess extends Reflectable, Invokable, Named, Accessible<MethodAccess> {

    /**
     * Reflection representation of the method.
     *
     * @return a reflection representation of the method
     * @see Class#getDeclaredMethods()
     */
    Method source();

    /**
     * Returns the type that declares this member.
     *
     * @return the unreflect type that declares this member
     */
    default UnreflectType declaringType() {
        return UnreflectType.of(source().getDeclaringClass());
    }

}
