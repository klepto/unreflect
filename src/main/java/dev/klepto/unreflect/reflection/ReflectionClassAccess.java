package dev.klepto.unreflect.reflection;

import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.UnreflectType;
import dev.klepto.unreflect.ClassAccess;
import dev.klepto.unreflect.ConstructorAccess;
import dev.klepto.unreflect.FieldAccess;
import dev.klepto.unreflect.MethodAccess;
import dev.klepto.unreflect.bytecode.BytecodeClassAccess;
import dev.klepto.unreflect.util.JdkInternals;
import lombok.RequiredArgsConstructor;
import lombok.With;
import one.util.streamex.StreamEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * Reflection-based implementation of {@link ClassAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@With
@RequiredArgsConstructor
public class ReflectionClassAccess<T> implements ClassAccess<T> {

    private final Class<T> source;
    private final Object object;

    @Override
    public ClassAccess<T> unreflect() {
        return new BytecodeClassAccess<>(this);
    }

    @Override
    public ClassAccess<T> reflect() {
        return this;
    }

    @Override
    public ClassAccess<T> bind(Object object) {
        return withObject(object);
    }

    @Override
    public int modifiers() {
        return source.getModifiers();
    }

    @Override
    public Reflectable parent() {
        return new ReflectionClassAccess<>(source.getSuperclass(), object);
    }

    @Override
    public StreamEx<Annotation> annotations() {
        return StreamEx.of(source.getDeclaredAnnotations());
    }

    @Override
    public UnreflectType type() {
        return UnreflectType.of(source);
    }

    @Override
    public Class<T> source() {
        return source;
    }

    @Override
    public T create(Object... args) {
        return constructor(args).create(args);
    }

    @Override
    public StreamEx<ConstructorAccess<T>> constructors() {
        return StreamEx.of(source.getDeclaredConstructors())
                .peek(constructor -> JdkInternals.setAccessible(constructor, true))
                .map(constructor ->
                        new ReflectionConstructorAccess<>(
                                this,
                                (Constructor<T>) constructor
                        )
                );
    }

    @Override
    public StreamEx<FieldAccess> fields() {
        return type().subTypes().flatArray(type -> type.toClass().getDeclaredFields())
                .peek(field -> JdkInternals.setAccessible(field, true))
                .map(field ->
                        new ReflectionFieldAccess(
                                this,
                                field,
                                object
                        )
                );
    }

    @Override
    public StreamEx<MethodAccess> methods() {
        return type().subTypes().flatArray(type -> type.toClass().getDeclaredMethods())
                .peek(method -> JdkInternals.setAccessible(method, true))
                .map(method ->
                        new ReflectionMethodAccess(
                                this,
                                method,
                                object
                        )
                );
    }

    @Override
    public String name() {
        return source.getName();
    }

    @Override
    public String toString() {
        return name();
    }

}
