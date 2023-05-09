package dev.klepto.unreflect.property;

import dev.klepto.unreflect.UnreflectType;
import one.util.streamex.StreamEx;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Represents a reflectable member (class, constructor, method, field, parameter).
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public interface Reflectable {

    /**
     * Gets the parent of this member.
     *
     * @return the parent member
     */
    Reflectable parent();

    /**
     * Returns a stream of annotations annotating this member.
     *
     * @return a stream of annotations present on this member
     */
    StreamEx<Annotation> annotations();

    /**
     * Gets annotation of a given type annotating this member, or null if annotation of a given type is not present.
     *
     * @param annotationClass the annotation type
     * @param <A>             the generic annotation type
     * @return an annotation of a given type, or null if annotation is not found
     */
    @Nullable
    default <A extends Annotation> A annotation(Class<A> annotationClass) {
        return annotations()
                .findFirst(annotation -> annotation.annotationType() == annotationClass)
                .map(annotationClass::cast)
                .orElse(null);
    }

    /**
     * Checks if member is annotated with given annotation.
     *
     * @param annotationClass the annotation type
     * @return true if member is annotated with given annotation
     */
    default boolean containsAnnotation(Class<? extends Annotation> annotationClass) {
        return annotation(annotationClass) != null;
    }

    /**
     * Returns {@link UnreflectType} representation of the member type.
     *
     * @return An unreflect type representation of the member type
     */
    UnreflectType type();

}
