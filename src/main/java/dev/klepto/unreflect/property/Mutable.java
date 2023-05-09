package dev.klepto.unreflect.property;

/**
 * Represents a mutable member (field) of a class.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface Mutable {

    /**
     * Gets value of this member.
     *
     * @param <T> a generic type for automatic casting
     * @return the value of this member
     */
    <T> T get();

    /**
     * Sets the value of this member.
     *
     * @param value the value of this member
     */
    void set(Object value);

}
