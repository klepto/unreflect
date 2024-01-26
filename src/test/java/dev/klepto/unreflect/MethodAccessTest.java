package dev.klepto.unreflect;

import dev.klepto.unreflect.bytecode.BytecodeMethodAccess;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link MethodAccess}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class MethodAccessTest {
    private final ClassAccess<TestSubject> classAccess = Unreflect.reflect(TestSubject.class);

    @Test
    @SneakyThrows
    public void testSource() {
        val method = TestSubject.class.getDeclaredMethod("method");
        assertEquals(method, classAccess.method("method").source());
    }

    @Test
    public void testInvoke() {
        assertEquals(3, classAccess.method("increase").<Integer>invoke(2));
        assertNull(classAccess.method("method").invoke());
    }

    @Test
    public void testUnreflect() {
        assertTrue(classAccess.method("method").unreflect() instanceof BytecodeMethodAccess);
        assertEquals(3, classAccess.method("increase").unreflect().<Integer>invoke(2));
        assertNull(classAccess.method("method").unreflect().invoke());
    }

    private static class TestSubject {
        private static int increase(int value) {
            return value + 1;
        }
        private static void method() {
        }
    }
}
