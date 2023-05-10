package dev.klepto.unreflect;

import dev.klepto.unreflect.bytecode.BytecodeFieldAccess;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class FieldAccessTest {

    private final ClassAccess<TestSubject> classAccess = Unreflect.reflect(TestSubject.class);

    @Test
    @SneakyThrows
    public void testSource() {
        val field = TestSubject.class.getDeclaredField("field");
        assertEquals(field, classAccess.field("field").source());
    }

    @Test
    public void testSetAndGet() {
        val field = classAccess.bind(new TestSubject()).field("field");
        assertEquals(0, field.<Integer>get());

        field.set(1337);
        assertEquals(1337, field.<Integer>get());
    }

    @Test
    public void testUnreflect() {
        val field = classAccess.bind(new TestSubject()).field("field").unreflect();
        assertTrue(field instanceof BytecodeFieldAccess);
        assertEquals(0, field.<Integer>get());

        field.set(1337);
        assertEquals(1337, field.<Integer>get());
    }

    private static class TestSubject {
        private int field;
    }

}
