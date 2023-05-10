package dev.klepto.unreflect.reflection;

import dev.klepto.unreflect.*;
import dev.klepto.unreflect.property.Reflectable;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Reflection-based implementation of {@link ParameterAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class ReflectionParameterAccess implements ParameterAccess {

    private final Reflectable parent;
    private final Parameter source;

    @Override
    public Reflectable parent() {
        return parent;
    }

    @Override
    public Parameter source() {
        return source;
    }

    @Override
    public String name() {
        return source.getName();
    }

    @Override
    public ConstructorAccess<?> constructor() {
        return (ConstructorAccess<?>) parent();
    }

    @Override
    public MethodAccess method() {
        return (MethodAccess) parent();
    }

    @Override
    public StreamEx<Annotation> annotations() {
        return StreamEx.of(source.getDeclaredAnnotations());
    }

    @Override
    public UnreflectType type() {
        return UnreflectType.of(source.getParameterizedType());
    }

    @Override
    public String toString() {
        return type().toString() + " " + name();
    }

}
