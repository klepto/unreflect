package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.property.Named;
import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.util.Parameters;
import one.util.streamex.StreamEx;

import javax.annotation.Nullable;

/**
 * Represents access to a class and it's members. Enables type, annotation, name, modifiers and members access.
 * Contains easy and intuitive lookup for constructors, fields and methods.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface ClassAccess<T> extends Reflectable, Named, Accessible<ClassAccess<T>> {

    /**
     * Returns the class that this class access represents.
     *
     * @return the represented class
     */
    Class<T> source();

    /**
     * Creates a new instance of a class, using runtime constructor lookup based on given parameter arguments.
     *
     * @param args the constructor arguments
     * @return an instance of represented class
     */
    T create(Object... args);

    /**
     * Returns a stream of all declared constructors in the represented class. Represented by {@link ConstructorAccess}.
     *
     * @return a stream of constructors in the represented class
     */
    StreamEx<ConstructorAccess<T>> constructors();

    /**
     * Returns a constructor by index, or null if constructor by given index doesn't exist. Note that declaration order
     * in the source code is not guaranteed to be preserved during runtime.
     *
     * @param index the constructor index
     * @return the declared constructor at a given index in the represented class
     */
    @Nullable
    default ConstructorAccess<T> constructor(int index) {
        return constructors().skip(index).findFirst().orElse(null);
    }

    /**
     * Returns the first declared constructor of represented class, or null if class has no constructors.
     *
     * @return the first available constructor in the represented class
     */
    default ConstructorAccess<T> constructor() {
        return constructor(0);
    }

    /**
     * Returns a constructor that loosely matches given parameter values or parameter types. This method can be
     * either supplied with array of values that you are going to pass to the constructor or array of classes that
     * represent value types. Neither values nor types have to be exact types, as long as they are assignable to a
     * constructor parameters a match will be found. May cause unexpected results with heavy constructor overloading.
     *
     * @param argsOrTypes an array of parameter values or parameter types
     * @return a constructor that accepts given parameter values or parameter types
     */
    default ConstructorAccess<T> constructor(Object... argsOrTypes) {
        return constructors().findFirst(constructor -> Parameters.matches(constructor, argsOrTypes)).orElse(null);
    }

    /**
     * Returns a stream of declared fields in the represented class. Represented by {@link FieldAccess}.
     *
     * @return a stream of fields in the represented class
     */
    StreamEx<FieldAccess> fields();

    /**
     * Returns a field by index, or null if field by given index doesn't exist. Field order in source code should always
     * match field order during runtime, which makes it rather safe to use source code as a reference for an index.
     *
     * @param index the field index
     * @return the declared field at a given index in the represented class
     */
    @Nullable
    default FieldAccess field(int index) {
        return fields().skip(index).findFirst().orElse(null);
    }

    /**
     * Returns a field by name, or null if field by given name doesn't exist. Field names are in classes guaranteed to
     * be unique.
     *
     * @param name the field name
     * @return the declared field with a given name in the represented class
     */
    default FieldAccess field(String name) {
        return fields().findFirst(field -> field.name().equals(name)).orElse(null);
    }

    /**
     * Returns a stream of declared methods in the represented class. Represented by {@link MethodAccess}.
     *
     * @return a stream of methods in the represented class
     */
    StreamEx<MethodAccess> methods();

    /**
     * Returns a stream of methods that loosely matches given parameter values or parameter types. Can be either supplied
     * with array of values that you are going to pass to the method or array of classes that represent value types.
     * Neither values nor types have to be exact types, as long as they are assignable to a method parameters a match
     * will be found.
     *
     * @param argsOrTypes an array of parameter values or parameter types
     * @return a stream of methods that accepts given parameter values or parameter types
     */
    default StreamEx<MethodAccess> methods(Object... argsOrTypes) {
        return methods().filter(method -> Parameters.matches(method, argsOrTypes));
    }

    /**
     * Returns a stream of declared methods that have a given name in the represented class.
     *
     * @param name the name of the methods
     * @return a stream of methods that have a given name
     */
    default StreamEx<MethodAccess> methods(String name) {
        return methods().filter(method -> method.name().equals(name));
    }

    /**
     * Returns a stream of declared methods that have a given name and match given parameter values or parameter types.
     *
     * @param name        the name of the methods
     * @param argsOrTypes an array of parameter values or parameter types
     * @return a stream of methods that have a given name and match given parameter values or types
     * @see ClassAccess#methods(Object...)
     */
    default StreamEx<MethodAccess> methods(String name, Object... argsOrTypes) {
        return methods().filter(method -> method.name().equals(name)).filter(method -> Parameters.matches(method, argsOrTypes));
    }

    /**
     * Returns a declared method in the represented class by a given index, or null if method by given index doesn't
     * exist. Note that method order in the source code is not guaranteed to be preserved during runtime.
     *
     * @param index the method index
     * @return a method at a given index in the represented class, or null if method at a given index doesn't exist
     */
    @Nullable
    default MethodAccess method(int index) {
        return methods().skip(index).findFirst().orElse(null);
    }

    /**
     * Returns the first declared method that matches given parameter values or types, or null if no matches are found.
     *
     * @param argsOrTypes an array of parameter values or parameter types
     * @return a method that accepts given parameter values or parameter types, or null if method is not found
     * @see ClassAccess#methods(Object...)
     */
    @Nullable
    default MethodAccess method(Object... argsOrTypes) {
        return methods(argsOrTypes).findFirst().orElse(null);
    }

    /**
     * Returns the first method that matches given method name, or null if no declared method with given name is found.
     *
     * @param name a method name
     * @return a method that has given name, or null if method is not found
     * @see ClassAccess#methods(String)
     */
    @Nullable
    default MethodAccess method(String name) {
        return methods(name).findFirst().orElse(null);
    }

    /**
     * Returns the first declared method that has a given name and match given parameter values or parameter types, or
     * null if matching method is not found.
     *
     * @param name        the name of the method
     * @param argsOrTypes an array of parameter values or parameter types
     * @return a method that have a given name and match given parameter values or types, or null if method is not found
     * @see ClassAccess#methods(String, Object...)
     */
    @Nullable
    default MethodAccess method(String name, Object... argsOrTypes) {
        return methods(name, argsOrTypes).findFirst().orElse(null);
    }

    /**
     * Returns class access of a superclass. Can be null if this class access represents {@link java.lang.Object}.
     *
     * @return the superclass access of represented class
     */
    @Nullable
    default ClassAccess<?> superclass() {
        return (ClassAccess<?>) parent();
    }

}
