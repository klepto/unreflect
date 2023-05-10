package dev.klepto.unreflect.bytecode.asm;

import lombok.Value;

import java.util.function.BiFunction;

/**
 * Contains implementation of direct access to method or constructor of a class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@Value
public class InvokableAccessor {

    BiFunction function;

    public Object invoke(Object object, Object... args) {
        return function.apply(object, (Object) args);
    }

}
