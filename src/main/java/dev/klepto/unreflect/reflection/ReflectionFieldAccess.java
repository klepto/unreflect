package dev.klepto.unreflect.reflection;

import dev.klepto.unreflect.bytecode.asm.AccessorGenerator;
import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.bytecode.BytecodeFieldAccess;
import dev.klepto.unreflect.UnreflectType;
import dev.klepto.unreflect.FieldAccess;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.With;
import lombok.val;
import one.util.streamex.StreamEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Reflection-based implementation of {@link FieldAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@With
@RequiredArgsConstructor
public class ReflectionFieldAccess implements FieldAccess {

    private final Reflectable parent;
    private final Field source;
    private final Object object;

    @Override
    public Reflectable parent() {
        return parent;
    }

    @Override
    public FieldAccess unreflect() {
        val accessor = AccessorGenerator.getInstance().generateMutableAccessor(source);
        return new BytecodeFieldAccess(this, accessor);
    }

    @Override
    public FieldAccess reflect() {
        return this;
    }

    @Override
    public FieldAccess bind(Object object) {
        return withObject(object);
    }

    @Override
    public int modifiers() {
        return source.getModifiers();
    }

    @Override
    @SneakyThrows
    public <T> T get() {
        return (T) source.get(object);
    }

    @Override
    @SneakyThrows
    public void set(Object value) {
        source.set(object, value);
    }

    @Override
    public StreamEx<Annotation> annotations() {
        return StreamEx.of(source.getDeclaredAnnotations());
    }

    @Override
    public UnreflectType type() {
        return UnreflectType.of(source.getGenericType());
    }

    @Override
    public String name() {
        return source.getName();
    }

    @Override
    public Field source() {
        return source;
    }

    public Object object() {
        return object;
    }

    @Override
    public String toString() {
        val signature = type().toString() + " " + name();
        return isStatic() ? "static " + signature : signature;
    }

}
