package dev.klepto.unreflect.property;

import dev.klepto.unreflect.ParameterAccess;
import one.util.streamex.StreamEx;

/**
 * Represents an invokable member (constructor, method) of a class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface Invokable {

    /**
     * Returns a stream of parameters associated with this member.
     *
     * @return a stream of parameters, or an empty stream if this member has no parameters
     */
    StreamEx<ParameterAccess> parameters();

    /**
     * Invokes this member with given arguments. For instanced access, you have call {@link Accessible#bind(Object)}
     * before invocation of this function. For static functions and constructors binding are ignored.
     *
     * @param args the arguments for invocation
     * @param <T>  a generic type for automatic casting
     * @return the result of member invocation, or null if result is void
     * @throws RuntimeException if exception occurred during invocation, or if member doesn't accept given arguments
     */
    <T> T invoke(Object... args) throws RuntimeException;

}
