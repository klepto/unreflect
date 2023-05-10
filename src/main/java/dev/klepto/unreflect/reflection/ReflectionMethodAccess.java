package dev.klepto.unreflect.reflection;

import dev.klepto.unreflect.bytecode.asm.AccessorGenerator;
import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.bytecode.BytecodeMethodAccess;
import dev.klepto.unreflect.UnreflectType;
import dev.klepto.unreflect.MethodAccess;
import dev.klepto.unreflect.ParameterAccess;
import dev.klepto.unreflect.util.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.With;
import lombok.val;
import one.util.streamex.StreamEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Reflection-based implementation of {@link MethodAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@With
@RequiredArgsConstructor
public class ReflectionMethodAccess implements MethodAccess {

    private final Reflectable parent;
    private final Method source;
    private final Object object;

    @Override
    @SneakyThrows
    public MethodAccess unreflect() {
        val accessor = AccessorGenerator.getInstance().generateInvokableAccessor(parent.type().toClass(), source);
        return new BytecodeMethodAccess(this, accessor);
    }

    @Override
    public MethodAccess reflect() {
        return this;
    }

    @Override
    public MethodAccess bind(Object object) {
        return withObject(object);
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
    @SneakyThrows
    public <T> T invoke(Object... args) {
        return (T) source.invoke(object, args);
    }

    @Override
    public Reflectable parent() {
        return parent;
    }

    @Override
    public StreamEx<Annotation> annotations() {
        return StreamEx.of(source.getAnnotations());
    }

    @Override
    public UnreflectType type() {
        return UnreflectType.of(source.getGenericReturnType());
    }

    @Override
    public String name() {
        return source.getName();
    }

    @Override
    public Method source() {
        return source;
    }

    public Object object() {
        return object;
    }

    @Override
    public String toString() {
        val signature = type().toClass().getSimpleName() + " " + name() + Parameters.toString(parameters().toList());
        return isStatic() ? "static " + signature : signature;
    }

}
