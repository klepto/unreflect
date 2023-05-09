package dev.klepto.unreflect;

import dev.klepto.unreflect.property.Accessible;
import dev.klepto.unreflect.property.Mutable;
import dev.klepto.unreflect.property.Named;
import dev.klepto.unreflect.property.Reflectable;

import java.lang.reflect.Field;

/**
 * Represents a declared field of a class. Enables setting and getting field values. Contains type, modifier, name and
 * annotation access.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface FieldAccess extends Reflectable, Mutable, Named, Accessible<FieldAccess> {

    /**
     * Reflection representation this field.
     *
     * @return a reflection representation this field
     * @see Class#getDeclaredFields()
     */
    Field source();

}
