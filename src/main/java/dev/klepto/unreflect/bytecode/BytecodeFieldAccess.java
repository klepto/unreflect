package dev.klepto.unreflect.bytecode;

import dev.klepto.unreflect.FieldAccess;
import dev.klepto.unreflect.bytecode.asm.MutableAccessor;
import dev.klepto.unreflect.reflection.ReflectionFieldAccess;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Bytecode access to a field of a class. Delegates all functions to {@link ReflectionFieldAccess} except the
 * {@link FieldAccess#set(Object)} and {@link FieldAccess#get()} functions for which it uses direct accessor.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class BytecodeFieldAccess implements FieldAccess {

    @Delegate(excludes = Overrides.class)
    private final ReflectionFieldAccess delegate;
    private final MutableAccessor accessor;

    @Override
    public FieldAccess unreflect() {
        return this;
    }

    @Override
    public FieldAccess reflect() {
        return delegate;
    }

    @Override
    public FieldAccess bind(Object object) {
        return new BytecodeFieldAccess((ReflectionFieldAccess) delegate.bind(object), accessor);
    }

    @Override
    public <T> T get() {
        return (T) accessor.get(object());
    }

    @Override
    public void set(Object value) {
        accessor.set(object(), value);
    }

    private interface Overrides {
        void unreflect();
        void reflect();
        void bind(Object object);
        void get();
        void set(Object value);

    }

}
