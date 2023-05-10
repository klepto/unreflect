package dev.klepto.unreflect.reflection;

import dev.klepto.unreflect.bytecode.asm.AccessorGenerator;
import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.bytecode.BytecodeContructorAccess;
import dev.klepto.unreflect.UnreflectType;
import dev.klepto.unreflect.ConstructorAccess;
import dev.klepto.unreflect.ParameterAccess;
import dev.klepto.unreflect.util.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.With;
import lombok.val;
import one.util.streamex.StreamEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * Reflection-based implementation of {@link ConstructorAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@With
@RequiredArgsConstructor
public class ReflectionConstructorAccess<T> implements ConstructorAccess<T> {

    private final Reflectable parent;
    private final Constructor<T> source;

    @Override
    public ConstructorAccess<T> unreflect() {
        val accessor = AccessorGenerator.getInstance().generateInvokableAccessor(source);
        return new BytecodeContructorAccess<>(this, accessor);
    }

    @Override
    public ConstructorAccess<T> reflect() {
        return this;
    }

    @Override
    public ConstructorAccess<T> bind(Object object) {
        return this;
    }

    @Override
    public int modifiers() {
        return source.getModifiers();
    }

    @Override
    public StreamEx<ParameterAccess> parameters() {
        return StreamEx.of(source.getParameters())
                .map(parameter -> new ReflectionParameterAccess(this, parameter));
    }

    @Override
    public Constructor<T> source() {
        return source;
    }

    @Override
    public T create(Object... args) {
        return invoke(args);
    }

    @Override
    @SneakyThrows
    public T invoke(Object... args) {
        return source.newInstance(args);
    }

    @Override
    public Reflectable parent() {
        return parent;
    }

    @Override
    public StreamEx<Annotation> annotations() {
        return StreamEx.of(source.getDeclaredAnnotations());
    }

    @Override
    public UnreflectType type() {
        return parent.type();
    }

    @Override
    public String toString() {
        return type().toClass().getSimpleName() + Parameters.toString(parameters().toList());
    }

}
