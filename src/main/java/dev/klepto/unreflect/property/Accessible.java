package dev.klepto.unreflect.property;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Represents an accessible member (constructor, method, field) of a class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface Accessible<T extends Accessible<T>> {

    /**
     * Returns code-generated representation of this member.
     *
     * @return a code-generated representation of this member
     */
    T unreflect();

    /**
     * Returns reflection representation of this member.
     *
     * @return a reflection representation of this member
     */
    T reflect();

    /**
     * Binds this member to a specific object instance for invocation/access.
     *
     * @return binds this member to a given object instance
     */
    T bind(Object object);

    /**
     * Returns the modifiers of this member.
     *
     * @return the modifiers of this member
     * @see Member#getModifiers()
     */
    int modifiers();

    /**
     * Returns true if member contains 'abstract' keyword.
     *
     * @return true if member is abstract
     */
    default boolean isAbstract() {
        return Modifier.isAbstract(modifiers());
    }

    /**
     * Returns true if member contains 'final' keyword.
     *
     * @return true if member is final
     */
    default boolean isFinal() {
        return Modifier.isFinal(modifiers());
    }

    /**
     * Returns true if member contains 'interface' keyword.
     *
     * @return true if member is an interface
     */
    default boolean isInterface() {
        return Modifier.isInterface(modifiers());
    }

    /**
     * Returns true if member contains 'native' keyword.
     *
     * @return true if member is native
     */
    default boolean isNative() {
        return Modifier.isNative(modifiers());
    }

    /**
     * Returns true if member contains 'private' keyword.
     *
     * @return true if member is private
     */
    default boolean isPrivate() {
        return Modifier.isPrivate(modifiers());
    }

    /**
     * Returns true if member contains 'protected' keyword.
     *
     * @return true if member is protected
     */
    default boolean isProtected() {
        return Modifier.isProtected(modifiers());
    }

    /**
     * Returns true if member contains 'public' keyword.
     *
     * @return true if member is public
     */
    default boolean isPublic() {
        return Modifier.isPublic(modifiers());
    }

    /**
     * Returns true if member contains 'static' keyword.
     *
     * @return true if member is static
     */
    default boolean isStatic() {
        return Modifier.isStatic(modifiers());
    }

    /**
     * Returns true if member contains 'strictfp' keyword.
     *
     * @return true if member is strict
     */
    default boolean isStrict() {
        return Modifier.isStrict(modifiers());
    }

    /**
     * Returns true if member contains 'synchronized' keyword.
     *
     * @return true if member is synchronized
     */
    default boolean isSynchronized() {
        return Modifier.isSynchronized(modifiers());
    }

    /**
     * Returns true if member contains 'transient' keyword.
     *
     * @return true if member is transient
     */
    default boolean isTransient() {
        return Modifier.isTransient(modifiers());
    }

    /**
     * Returns true if member contains 'volatile' keyword.
     *
     * @return true if member is volatile
     */
    default boolean isVolatile() {
        return Modifier.isVolatile(modifiers());
    }

    /**
     * Returns true if member contains 'enum' keyword.
     *
     * @return true if member is enum
     */
    default boolean isEnum() {
        return (modifiers() & 0x00004000) != 0;
    }


}
