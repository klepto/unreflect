package dev.klepto.unreflect.bytecode.asm;


import lombok.Value;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Contains implementations of direct access to a field of class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@Value
public class MutableAccessor {

    Function getter;
    BiConsumer setter;

    public Object get(Object object) {
        return getter.apply(object);
    }

    public void set(Object object, Object value) {
        setter.accept(object, value);
    }

}
