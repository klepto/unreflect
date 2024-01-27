package dev.klepto.unreflect;

import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link UnreflectType}.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class UnreflectTypeTest {

    @Test
    @SneakyThrows
    public void testOf() {
        val object = new TestSubjectC();
        val type = (Type) object.getClass();
        val typeToken = TypeToken.of(TestSubjectC.class);
        val field = TestSubjectC.class.getDeclaredField("field");
        val method = TestSubjectC.class.getDeclaredMethod("method", TestSubjectC.class);
        val parameter = method.getParameters()[0];

        val unreflectType = UnreflectType.of(TestSubjectC.class);
        val expected = Arrays.asList(
                unreflectType, unreflectType, unreflectType, unreflectType, unreflectType, unreflectType
        );
        val actual = Arrays.asList(
                UnreflectType.of(object), UnreflectType.of(type), UnreflectType.of(typeToken),
                UnreflectType.of(field), UnreflectType.of(method), UnreflectType.of(parameter)
        );

        assertIterableEquals(expected, actual);
    }

    @Test
    public void testAllocate() {
        val type = UnreflectType.of(TestSubjectC.class);
        val instance = type.<TestSubjectC>allocate();
        assertNotNull(instance);
        assertFalse(instance.booleanField);
    }

    @Test
    public void testName() {
        val type = UnreflectType.of(TestSubject.class);
        assertEquals("dev.klepto.unreflect.UnreflectTypeTest$TestSubject", type.name());
    }

    @Test
    public void testSuperTypes() {
        val type = UnreflectType.of(TestSubjectC.class);
        assertEquals(UnreflectType.of(TestSubjectA.class), type.superType());
        assertEquals(UnreflectType.of(Object.class), type.superType(1));

        val expected = Arrays.asList(UnreflectType.of(TestSubjectA.class), UnreflectType.of(Object.class));
        val actual = type.superTypes().toList();
        assertIterableEquals(expected, actual);
    }

    @Test
    public void testSubTypes() {
        val type = UnreflectType.of(TestSubjectC.class);
        val subTypes = type.subTypes().toList();
        val typeToken = new TypeToken<TestSubject<Short, Integer, Long>>() {};

        assertTrue(subTypes.contains(UnreflectType.of(TestSubjectC.class)));
        assertTrue(subTypes.contains(UnreflectType.of(TestSubjectA.class)));
        assertTrue(subTypes.contains(UnreflectType.of(typeToken)));
        assertTrue(subTypes.contains(UnreflectType.of(Object.class)));
    }

    @Test
    public void testGenericTypes() {
        val typeA = UnreflectType.of(TestSubjectA.class).subType(1);
        assertNotNull(typeA);
        assertEquals(typeA.toClass(), TestSubject.class);

        val expected = Arrays.asList(
                UnreflectType.of(Short.class),
                UnreflectType.of(Integer.class),
                UnreflectType.of(Long.class)
        );
        assertEquals(expected.get(2), typeA.genericType(2));
        assertIterableEquals(expected, typeA.genericTypes().toList());

        val typeB = UnreflectType.of(TestSubjectB.class).subType(1);
        assertNotNull(typeB);
        assertEquals(typeB.toClass(), TestSubject.class);
        assertEquals(Set.class, typeB.genericType(0).toClass());
        assertEquals(List.class, typeB.genericType(0).genericType(0).toClass());
        assertEquals(Short.class, typeB.genericType(0).genericType(0).genericType(0).toClass());
    }

    @Test
    public void testMatches() {
        val typeB = UnreflectType.of(TestSubjectB.class);
        val typeC = UnreflectType.of(TestSubjectC.class);

        assertFalse(typeB.matches(Object.class));
        assertTrue(typeC.matches(Object.class));

        assertTrue(typeB.matches(TestSubject.class));
        assertFalse(typeB.matchesExact(TestSubject.class));
        assertTrue(typeB.matchesExact(TestSubjectB.class));
        assertTrue(typeC.matches(new Object()));
    }

    @Test
    public void testArray() {
        val type = UnreflectType.of(boolean[][].class);
        assertTrue(type.isArray());
        assertEquals(boolean[].class, type.componentType().toClass());
        assertEquals(boolean.class, type.componentType().componentType().toClass());
    }

    @Test
    public void testPrimitive() {
        assertTrue(UnreflectType.of(boolean.class).isPrimitive());
        assertTrue(UnreflectType.of(byte.class).isPrimitive());
        assertTrue(UnreflectType.of(char.class).isPrimitive());
        assertTrue(UnreflectType.of(double.class).isPrimitive());
        assertTrue(UnreflectType.of(float.class).isPrimitive());
        assertTrue(UnreflectType.of(int.class).isPrimitive());
        assertTrue(UnreflectType.of(long.class).isPrimitive());
        assertTrue(UnreflectType.of(short.class).isPrimitive());
        assertTrue(UnreflectType.of(void.class).isPrimitive());
    }

    @Test
    public void testInterfaces() {
        val type = UnreflectType.of(TestSubjectC.class);
        val interfaceType = type.interfaceType();
        assertNotNull(interfaceType);
        assertEquals(TestSubject.class, interfaceType.toClass());
        assertTrue(interfaceType.isInterface());
    }

    private interface TestSubject<A, B, C> {
    }

    private static class TestSubjectA implements TestSubject<Short, Integer, Long> {
    }

    private interface TestSubjectB extends TestSubject<Set<List<Short>>, List<Integer>, Long[]> {
    }

    private static class TestSubjectC extends TestSubjectA {
        TestSubjectC field;
        boolean booleanField;
        private TestSubjectC() {
            booleanField = true;
        }
        TestSubjectC method(TestSubjectC parameter) {
            return null;
        }
    }

}
