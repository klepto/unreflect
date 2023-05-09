package dev.klepto.unreflect.bytecode.asm;

/**
 * Implements direct access to method or constructor of a class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface InvokableAccessor {

    Object invoke(Object object, Object... args);

}
