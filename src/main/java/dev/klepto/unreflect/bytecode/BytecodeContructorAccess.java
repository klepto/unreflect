package dev.klepto.unreflect.bytecode;

import dev.klepto.unreflect.ConstructorAccess;
import dev.klepto.unreflect.bytecode.asm.InvokableAccessor;
import dev.klepto.unreflect.reflection.ReflectionConstructorAccess;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Bytecode access to a constructor of a class. Delegates all functions to {@link ReflectionConstructorAccess} except
 * the {@link ConstructorAccess#invoke(Object...)} function for which it uses direct accessor.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class BytecodeContructorAccess<T> implements ConstructorAccess<T> {

    @Delegate(excludes = Overrides.class)
    private final ReflectionConstructorAccess<T> delegate;
    private final InvokableAccessor accessor;

    @Override
    public ConstructorAccess<T> unreflect() {
        return this;
    }

    @Override
    public ConstructorAccess<T> reflect() {
        return delegate;
    }

    @Override
    public T invoke(Object... args) {
        return (T) accessor.invoke(null, args);
    }

    private interface Overrides {
        void unreflect();

        void reflect();

        void invoke(Object... args);
    }

}
