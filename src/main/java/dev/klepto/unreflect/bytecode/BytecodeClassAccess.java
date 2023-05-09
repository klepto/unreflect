package dev.klepto.unreflect.bytecode;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.property.Reflectable;
import dev.klepto.unreflect.ClassAccess;
import dev.klepto.unreflect.ConstructorAccess;
import dev.klepto.unreflect.FieldAccess;
import dev.klepto.unreflect.MethodAccess;
import dev.klepto.unreflect.reflection.ReflectionClassAccess;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import one.util.streamex.StreamEx;

/**
 * Bytecode version of {@link ClassAccess}. All members returned by this class generate bytecode for direct access.
 * Since bytecode generation is very costly use this only when you need direct access to the entire class, otherwise
 * use of {@link ReflectionClassAccess} is advised and selectively converting members to bytecode access by calling
 * {@link Accessible#unreflect()}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class BytecodeClassAccess<T> implements ClassAccess<T> {

    @Delegate(excludes = Overrides.class)
    private final ReflectionClassAccess<T> delegate;

    @Override
    public ClassAccess<T> unreflect() {
        return this;
    }

    @Override
    public ClassAccess<T> reflect() {
        return delegate;
    }

    @Override
    public ClassAccess<T> bind(Object object) {
        return delegate.bind(object).unreflect();
    }

    @Override
    public Reflectable parent() {
        return ((ClassAccess<?>)delegate.parent()).unreflect();
    }

    @Override
    public StreamEx<ConstructorAccess<T>> constructors() {
        return delegate.constructors().map(Accessible::unreflect);
    }

    @Override
    public StreamEx<FieldAccess> fields() {
        return delegate.fields().map(Accessible::unreflect);
    }

    @Override
    public StreamEx<MethodAccess> methods() {
        return delegate.methods().map(Accessible::unreflect);
    }

    private interface Overrides {
        void unreflect();
        void reflect();
        void bind(Object object);
        void parent();
        void constructors();
        void constructor();
        void constructor(int index);
        void constructor(Object[] argsOrTypes);
        void fields();
        void field(int index);
        void field(String name);
        void methods();
        void methods(Object[] argsOrTypes);
        void methods(String name);
        void methods(String name, Object[] argsOrTypes);
        void method(int index);
        void method(Object[] argsOrTypes);
        void method(String name);
        void method(String name, Object[] argsOrTypes);
    }

}
