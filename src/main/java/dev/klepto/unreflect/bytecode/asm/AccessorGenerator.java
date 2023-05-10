package dev.klepto.unreflect.bytecode.asm;

import com.google.common.primitives.Primitives;
import dev.klepto.unreflect.util.JdkInternals;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import one.util.streamex.StreamEx;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

/**
 * Bytecode generation of field, constructor and method accessors. Utilizes the JDK's internal MagicAccessorImpl class,
 * which allows to bypass all security checks and directly access private and otherwise unreachable members.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class AccessorGenerator {

    @Getter(lazy = true)
    private static final AccessorGenerator instance = new AccessorGenerator();

    private final AtomicInteger accessorIndex = new AtomicInteger();
    private final String superClass = JdkInternals.getMagicAccessorImpl().getName();

    @SneakyThrows
    public MutableAccessor generateMutableAccessor(Field field) {
        val className = getNextClassName();
        val cw = new ClassWriter(COMPUTE_MAXS);
        generateHeader(cw, className, Function.class.getName(), BiConsumer.class.getName());

        val fieldOwner = Type.getInternalName(field.getDeclaringClass());
        val fieldName = field.getName();
        val fieldType = field.getType();
        val fieldModifiers = field.getModifiers();
        val fieldDescriptor = Type.getDescriptor(field.getType());

        // Getter.
        {
            val methodDescriptor = Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class));
            val fieldOpcode = Modifier.isStatic(fieldModifiers) ? GETSTATIC : GETFIELD;
            val mv = cw.visitMethod(ACC_PUBLIC, "apply", methodDescriptor, null, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, fieldOwner);
            mv.visitFieldInsn(fieldOpcode, fieldOwner, fieldName, fieldDescriptor);
            if (isPrimitive(fieldType)) {
                generateWrapPrimitive(mv, fieldType);
            }
            mv.visitInsn(ARETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        // Setter.
        {
            val methodDescriptor = Type.getMethodDescriptor(
                    Type.getType(void.class),
                    Type.getType(Object.class),
                    Type.getType(Object.class)
            );
            val fieldOpcode = Modifier.isStatic(field.getModifiers()) ? PUTSTATIC : PUTFIELD;
            val mv = cw.visitMethod(ACC_PUBLIC, "accept", methodDescriptor, null, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, fieldOwner);
            mv.visitVarInsn(ALOAD, 2);
            if (isPrimitive(field.getType())) {
                generateUnwrapPrimitive(mv, fieldType);
            } else {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(fieldType));
            }
            mv.visitFieldInsn(fieldOpcode, fieldOwner, fieldName, fieldDescriptor);
            mv.visitInsn(RETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }

        // Finish class generation.
        cw.visitEnd();

        // Load accessor.
        val accessor = loadAccessor(field.getDeclaringClass(), className, cw.toByteArray());
        return new MutableAccessor((Function) accessor, (BiConsumer) accessor);
    }

    public InvokableAccessor generateInvokableAccessor(Method method) {
        return _generateInvokableAccessor(method);
    }

    public InvokableAccessor generateInvokableAccessor(Constructor<?> constructor) {
        return _generateInvokableAccessor(constructor);
    }

    private InvokableAccessor _generateInvokableAccessor(Member member) {
        checkArgument(member instanceof Constructor<?> || member instanceof Method);

        val className = getNextClassName();
        val cw = new ClassWriter(COMPUTE_MAXS);
        generateHeader(cw, className, BiFunction.class.getName());

        val invokeDescriptor = Type.getMethodDescriptor(
                Type.getType(Object.class),
                Type.getType(Object.class),
                Type.getType(Object.class)
        );
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_INTERFACE, "apply", invokeDescriptor, null, null);

        val parameterCount = member instanceof Constructor
                ? ((Constructor<?>) member).getParameterCount()
                : ((Method) member).getParameterCount();
        val exceptionType = Type.getInternalName(RuntimeException.class);
        val executionLabel = new Label();

        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
        mv.visitInsn(ARRAYLENGTH);
        mv.visitLdcInsn(parameterCount);
        mv.visitJumpInsn(IF_ICMPEQ, executionLabel);
        mv.visitTypeInsn(NEW, exceptionType);
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Parameter count mismatch for: " + member);
        mv.visitMethodInsn(INVOKESPECIAL, exceptionType, "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);

        mv.visitLabel(executionLabel);
        if (member instanceof Constructor) {
            val constructor = (Constructor<?>) member;
            val constructorOwner = Type.getInternalName(constructor.getDeclaringClass());
            val constructorDescriptor = Type.getConstructorDescriptor(constructor);
            val constructorParameters = constructor.getParameters();

            mv.visitTypeInsn(NEW, constructorOwner);
            mv.visitInsn(DUP);
            for (int i = 0; i < constructorParameters.length; i++) {
                val parameter = constructorParameters[i];
                generateArrayParameter(mv, 2, i, parameter.getType());
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, constructorOwner, "<init>", constructorDescriptor, false);
        } else {
            val method = (Method) member;
            val methodOwner = Type.getInternalName(method.getDeclaringClass());
            val methodName = method.getName();
            val methodDescriptor = Type.getMethodDescriptor(method);
            val methodType = method.getReturnType();
            val methodStatic = Modifier.isStatic(method.getModifiers());
            val methodOpcode = methodStatic ? INVOKESTATIC : INVOKEVIRTUAL;
            val methodParameters = method.getParameters();

            if (!methodStatic) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, methodOwner);
            }

            for (int i = 0; i < methodParameters.length; i++) {
                val parameter = methodParameters[i];
                generateArrayParameter(mv, 2, i, parameter.getType());
            }

            mv.visitMethodInsn(methodOpcode, methodOwner, methodName, methodDescriptor, false);
            if (methodType.isPrimitive() && methodType != void.class) {
                generateWrapPrimitive(mv, method.getReturnType());
            } else if (methodType == void.class) {
                mv.visitInsn(ACONST_NULL);
            }
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        // Finish class generation.
        cw.visitEnd();

        // Load accessor.
        val accessor = loadAccessor(member.getDeclaringClass(), className, cw.toByteArray());
        return new InvokableAccessor((BiFunction) accessor);
    }

    @SneakyThrows
    public Object loadAccessor(Class<?> context, String className, byte[] bytecode) {
        val accessorClass = JdkInternals.defineClass(context.getClassLoader(), className, bytecode);
        return JdkInternals.allocateInstance(accessorClass);
    }

    public String getNextClassName() {
        val baseClass = superClass + "$unreflect";
        return baseClass + accessorIndex.getAndIncrement();
    }

    public void generateHeader(ClassWriter cw, String className, String... interfaceNames) {
        val interfaces = StreamEx.of(interfaceNames).map(AccessorGenerator::getInternal).toArray(String.class);

        cw.visit(
                V1_8,
                ACC_PUBLIC,
                getInternal(className),
                null,
                getInternal(superClass),
                interfaces
        );

        val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, getInternal(superClass), "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public void generateArrayParameter(MethodVisitor mv, int arraySlot, int valueIndex, Class<?> parameterType) {
        mv.visitVarInsn(ALOAD, arraySlot);
        mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
        mv.visitLdcInsn(valueIndex);
        mv.visitInsn(AALOAD);
        if (parameterType.isPrimitive()) {
            generateUnwrapPrimitive(mv, parameterType);
        } else {
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(parameterType));
        }
    }

    public void generateWrapPrimitive(MethodVisitor mv, Class<?> type) {
        val wrapped = Primitives.wrap(type);
        val unwrapped = Primitives.unwrap(type);
        val owner = Type.getInternalName(wrapped);
        val descriptor = Type.getMethodDescriptor(Type.getType(wrapped), Type.getType(unwrapped));

        mv.visitMethodInsn(
                INVOKESTATIC,
                owner,
                "valueOf",
                descriptor,
                false
        );
    }

    public void generateUnwrapPrimitive(MethodVisitor mv, Class<?> type) {
        val wrapped = Primitives.wrap(type);
        val unwrapped = Primitives.unwrap(type);
        val name = unwrapped.getSimpleName() + "Value";
        val owner = Type.getInternalName(wrapped);
        val descriptor = Type.getMethodDescriptor(Type.getType(unwrapped));

        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(wrapped));
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                owner,
                name,
                descriptor,
                false
        );
    }

    private static boolean isPrimitive(Class<?> type) {
        return Primitives.allPrimitiveTypes().contains(type);
    }

    private static String getInternal(String className) {
        return className.replace('.', '/');
    }

}