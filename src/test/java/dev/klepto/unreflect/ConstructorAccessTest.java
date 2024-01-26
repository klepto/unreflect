package dev.klepto.unreflect;

import dev.klepto.unreflect.bytecode.BytecodeContructorAccess;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Tests for {@link ConstructorAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class ConstructorAccessTest {

    private final ClassAccess<TestSubject> classAccess = Unreflect.reflect(TestSubject.class);

    @Test
    public void testSource() {
        val constructor = TestSubject.class.getDeclaredConstructors()[0];
        assertEquals(constructor, classAccess.constructor().source());
    }

    @Test
    public void testCreate() {
        val instanceA = classAccess.constructor().create();
        val instanceB = classAccess.constructor(String.class).create("");
        val instanceC = classAccess.constructor(int.class).create(0);

        assertNotNull(instanceA);
        assertNotNull(instanceB);
        assertNotNull(instanceC);

        assertNotSame(instanceA, instanceB);
        assertNotSame(instanceB, instanceC);
        assertNotSame(instanceC, instanceA);
    }

    @Test
    public void testUnreflect() {
        val constructorAccess = classAccess.constructor().unreflect();
        assertTrue(constructorAccess instanceof BytecodeContructorAccess);

        val instance = constructorAccess.create();
        assertNotNull(instance);
    }


    private static class TestSubject {
        private TestSubject() {
        }
        private TestSubject(String value) {
        }

        private TestSubject(int value) {
        }
    }

}
