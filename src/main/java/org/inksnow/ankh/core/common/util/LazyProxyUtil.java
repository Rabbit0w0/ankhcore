package org.inksnow.ankh.core.common.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.inksnow.ankh.core.api.util.DcLazy;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class LazyProxyUtil {
  private static final String DCLAZY_INTERNAL_NAME = Type.getInternalName(DcLazy.class);
  private static final String DCLAZY_DESCRIPTOR = Type.getDescriptor(DcLazy.class);
  private static final AtomicLong idAllocator = new AtomicLong();
  private static final Map<String, Class<?>> innerClasses = new HashMap<>();

  static {
    addInnerClass(DcLazy.class);
  }

  private static void addInnerClass(Class<?> clazz) {
    innerClasses.put(clazz.getName(), clazz);
  }

  @SneakyThrows
  public <T> T generate(Class<T> clazz, DcLazy<? extends T> dcLazy) {
    val classLoader = clazz.getClassLoader();
    val classWriter = new ClassWriterWithClassLoader(classLoader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    val interfaceInternalName = Type.getInternalName(clazz);
    val interfaceDescriptor = Type.getDescriptor(clazz);
    val generateClassInternalName = (interfaceInternalName.startsWith("java/") ? "$" : "") + interfaceInternalName + "$ankh-core-asm-lazy-proxy$" + idAllocator.incrementAndGet();
    classWriter.visit(
        Opcodes.V1_8,
        Opcodes.ACC_PUBLIC,
        generateClassInternalName,
        null,
        "java/lang/Object",
        new String[]{interfaceInternalName}
    );
    val generateFieldName = "delegate$ankh-core-asm-lazy-proxy$" + idAllocator.incrementAndGet();

    classWriter.visitField(
        Opcodes.ACC_PUBLIC,
        generateFieldName,
        DCLAZY_DESCRIPTOR,
        null,
        null
    );
    {
      val methodVisitor = classWriter.visitMethod(
          Opcodes.ACC_PUBLIC,
          "<init>",
          "(" + DCLAZY_DESCRIPTOR + ")V",
          null,
          null
      );
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
      methodVisitor.visitMethodInsn(
          Opcodes.INVOKESPECIAL,
          "java/lang/Object",
          "<init>",
          "()V",
          false
      );
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
      methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, generateClassInternalName, generateFieldName, DCLAZY_DESCRIPTOR);
      methodVisitor.visitInsn(Opcodes.RETURN);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
    }
    val delegateMethodName = "delegate$ankh-core-asm-lazy-proxy$" + idAllocator.incrementAndGet();
    val delegateMethodDescriptor = "()" + interfaceDescriptor;
    {
      val methodVisitor = classWriter.visitMethod(
          Opcodes.ACC_PUBLIC,
          delegateMethodName,
          delegateMethodDescriptor,
          null,
          null
      );
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
      methodVisitor.visitFieldInsn(Opcodes.GETFIELD, generateClassInternalName, generateFieldName, DCLAZY_DESCRIPTOR);
      methodVisitor.visitMethodInsn(
          Opcodes.INVOKEVIRTUAL,
          DCLAZY_INTERNAL_NAME,
          "get",
          "()Ljava/lang/Object;",
          false
      );
      methodVisitor.visitInsn(Opcodes.ARETURN);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
    }
    for (val method : clazz.getDeclaredMethods()) {
      val isFinal = (method.getModifiers() & Opcodes.ACC_FINAL) != 0;
      if (isFinal) {
        continue;
      }
      val methodType = Type.getType(method);
      val methodName = method.getName();
      val methodDescriptor = methodType.getDescriptor();
      val methodVisitor = classWriter.visitMethod(
          Opcodes.ACC_PUBLIC,
          methodName,
          methodDescriptor,
          null,
          null
      );
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
      methodVisitor.visitMethodInsn(
          Opcodes.INVOKESPECIAL,
          generateClassInternalName,
          delegateMethodName,
          delegateMethodDescriptor,
          false
      );
      var varId = 1;
      for (Type argumentType : methodType.getArgumentTypes()) {
        val loadOpcode = argumentType.getOpcode(Opcodes.ILOAD);
        methodVisitor.visitVarInsn(loadOpcode, varId);
        varId += argumentType.getSize();
      }
      methodVisitor.visitMethodInsn(
          Opcodes.INVOKEINTERFACE,
          interfaceInternalName,
          methodName,
          methodDescriptor,
          true
      );
      methodVisitor.visitInsn(methodType.getReturnType().getOpcode(Opcodes.IRETURN));
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
    }
    classWriter.visitEnd();
    val bytes = classWriter.toByteArray();
    val defineClass = new CodeDefClassLoader(classLoader)
        .define(generateClassInternalName.replace('/', '.'), bytes, 0, bytes.length);
    return (T) defineClass.getConstructor(DcLazy.class).newInstance(dcLazy);
  }

  private class CodeDefClassLoader extends ClassLoader {
    public CodeDefClassLoader(ClassLoader parent) {
      super(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      val innerClass = innerClasses.get(name);
      if (innerClass != null) {
        return innerClass;
      }
      return super.loadClass(name, resolve);
    }

    public Class<?> define(String name, byte[] b, int off, int len) {
      return defineClass(name, b, off, len);
    }
  }
}
