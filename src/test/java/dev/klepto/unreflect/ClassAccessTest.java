package dev.klepto.unreflect;

import dev.klepto.unreflect.bytecode.BytecodeClassAccess;
import dev.klepto.unreflect.bytecode.BytecodeContructorAccess;
import dev.klepto.unreflect.bytecode.BytecodeFieldAccess;
import dev.klepto.unreflect.bytecode.BytecodeMethodAccess;
import dev.klepto.unreflect.reflection.ReflectionClassAccess;
import dev.klepto.unreflect.reflection.ReflectionConstructorAccess;
import dev.klepto.unreflect.reflection.ReflectionFieldAccess;
import dev.klepto.unreflect.reflection.ReflectionMethodAccess;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class ClassAccessTest {

    private final TestSubject subject = new TestSubject();
    private final ClassAccess<TestSubject> classAccess = Unreflect.reflect(subject);

    @Test
    public void testSource() {
        assertEquals(TestSubject.class, classAccess.source());
    }

    @Test
    public void testDeclaringType() {
        assertEquals(getClass(), classAccess.declaringType().toClass());
    }

    @Test
    public void testUnreflect() {
        val unreflected = classAccess.unreflect();
        assertSame(unreflected.getClass(), BytecodeClassAccess.class);
        assertTrue(unreflected.constructors().allMatch(member -> member.getClass() == BytecodeContructorAccess.class));
        assertTrue(unreflected.fields().allMatch(member -> member.getClass() == BytecodeFieldAccess.class));
        assertTrue(unreflected.methods().allMatch(member -> member.getClass() == BytecodeMethodAccess.class));

        val reflected = unreflected.reflect();
        assertSame(reflected.getClass(), ReflectionClassAccess.class);
        assertTrue(reflected.constructors().allMatch(member -> member.getClass() == ReflectionConstructorAccess.class));
        assertTrue(reflected.fields().allMatch(member -> member.getClass() == ReflectionFieldAccess.class));
        assertTrue(reflected.methods().allMatch(member -> member.getClass() == ReflectionMethodAccess.class));
    }

    @Test
    public void testCreate() {
        val instanceA = classAccess.create();
        val instanceB = classAccess.create("");

        assertNotNull(instanceA);
        assertNotNull(instanceB);
        assertNotEquals(instanceA, instanceB);
    }

    @Test
    public void testConstructors() {
        val constructors = classAccess.constructors().toList();
        assertTrue(3 <= constructors.size());
        assertEquals(constructors.get(0).source(), classAccess.constructor().source());
        assertEquals(constructors.get(1).source(), classAccess.constructor(1).source());

        val stringValueConstructors = classAccess.constructors("").toList();
        val stringTypeConstructors = classAccess.constructors(String.class).toList();
        assertEquals(2, stringValueConstructors.size());
        assertEquals(2, stringTypeConstructors.size());

        val objectTypeConstructors = classAccess.constructors(Object.class).toList();
        assertEquals(1, objectTypeConstructors.size());
    }

    @Test
    public void testFields() {
        val fields = classAccess.fields().toList();
        assertEquals(2, fields.size());
        assertEquals(fields.get(0).source(), classAccess.field(0).source());
        assertEquals(fields.get(1).source(), classAccess.field(1).source());
        assertEquals(fields.get(0).source(), classAccess.field("field").source());
        assertEquals(fields.get(1).source(), classAccess.field("fieldStatic").source());
    }

    @Test
    public void testMethods() {
        val declaringFilter = (Predicate<MethodAccess>) member -> member.declaringType().matchesExact(TestSubject.class);
        val allMethods = classAccess.methods().toList();
        val declaredMethods = classAccess.methods().filter(declaringFilter).toList();
        val stringMethods = classAccess.methods(String.class).filter(declaringFilter).toList();
        val staticMethods = classAccess.methods("methodStatic").toList();
        val staticStringMethods = classAccess.methods("methodStatic", String.class).toList();
        val staticObjectMethods = classAccess.methods("methodStatic", Object.class).toList();

        assertEquals(3, declaredMethods.size());
        assertEquals(2, stringMethods.size());
        assertEquals(2, staticMethods.size());
        assertEquals(2, staticStringMethods.size());
        assertEquals(1, staticObjectMethods.size());
        assertEquals(allMethods.get(0).source(), classAccess.method(0).source());
        assertEquals(staticObjectMethods.get(0).source(), classAccess.method(Object.class).source());
        assertEquals(staticStringMethods.get(0).source(), classAccess.method("methodStatic").source());
        assertEquals(staticObjectMethods.get(0).source(), classAccess.method("methodStatic", Object.class).source());
    }

    @Test
    public void testAnnotations() {
        assertEquals(1, classAccess.annotations().count());
        assertTrue(classAccess.containsAnnotation(TestAnnotation.class));
    }

    @Test
    public void testModifiers() {
        assertTrue(classAccess.isStatic());
        assertTrue(classAccess.isPrivate());
    }

    @Test
    public void testSuperclass() {
        assertEquals(Object.class, classAccess.superclass().source());
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {
    }

    @TestAnnotation
    private static class TestSubject {
        private Object field;
        private static Object fieldStatic;

        private TestSubject() {
        }

        private TestSubject(String value) {
        }

        private TestSubject(Object value) {
        }

        private void method() {
        }

        private static void methodStatic(String value) {
        }
        private static void methodStatic(Object value) {
        }
    }

}
