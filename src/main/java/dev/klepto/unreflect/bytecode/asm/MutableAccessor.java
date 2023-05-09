package dev.klepto.unreflect.bytecode.asm;


/**
 * Implements direct access to a field of class. Equivalent of defining a setter and a getter.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface MutableAccessor {

    Object get(Object object);

    void set(Object object, Object value);

}
