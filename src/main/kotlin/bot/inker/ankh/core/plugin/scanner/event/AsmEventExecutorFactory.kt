package bot.inker.ankh.core.plugin.scanner.event

import bot.inker.acj.JvmHacker
import bot.inker.ankh.core.common.util.ClassWriterWithClassLoader
import org.bukkit.plugin.EventExecutor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.lang.invoke.CallSite
import java.lang.invoke.ConstantCallSite
import java.lang.invoke.MethodType
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

object AsmEventExecutorFactory {
  private val innerClasses = Collections.synchronizedMap(HashMap<String, Class<*>>())
  private val callSites = Collections.synchronizedMap(HashMap<String, CallSite>())
  private val idAllocator = AtomicLong()

  fun generateDynamicExecutor(
    ownerClass: Class<*>,
    owner: Any?,
    methodName: String,
    rawType: Type,
  ): EventExecutor {
    val classLoader = ownerClass.classLoader
    val classNode = ClassNode()
    classNode.version = Opcodes.V1_8
    classNode.access = Opcodes.ACC_PUBLIC
    classNode.name =
      "bot/inker/ankh/core/\$generated\$/\$generated-dynamic-event-executor\$" + idAllocator.incrementAndGet()
    classNode.superName = "java/lang/Object"
    classNode.interfaces = listOf("org/bukkit/plugin/EventExecutor")
    classNode.fields.add(
      FieldNode(
        Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC,
        "callsite",
        "Ljava/lang/invoke/CallSite;",
        null,
        null
      )
    )

    MethodNode(
      Opcodes.ACC_PUBLIC,
      "<init>",
      "(Ljava/lang/invoke/CallSite;)V",
      null,
      null
    ).also(classNode.methods::add).instructions.let { insn ->
      insn.add(VarInsnNode(Opcodes.ALOAD, 0))
      insn.add(
        MethodInsnNode(
          Opcodes.INVOKESPECIAL,
          "java/lang/Object",
          "<init>",
          "()V"
        )
      )
      insn.add(VarInsnNode(Opcodes.ALOAD, 1))
      insn.add(FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, "callsite", "Ljava/lang/invoke/CallSite;"))
      insn.add(InsnNode(Opcodes.RETURN))
    }

    MethodNode(
      Opcodes.ACC_PUBLIC,
      "execute",
      "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Event;)V",
      null,
      null
    ).also(classNode.methods::add).instructions.let { insn ->
      insn.add(VarInsnNode(Opcodes.ALOAD, 2))
      insn.add(TypeInsnNode(Opcodes.CHECKCAST, rawType.argumentTypes[0].internalName))
      insn.add(
        InvokeDynamicInsnNode(
          methodName,
          rawType.descriptor,
          Handle(
            Opcodes.H_INVOKESTATIC,
            classNode.name,
            "callsite",
            "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
            false
          )
        )
      )
      insn.add(InsnNode(Opcodes.RETURN))
    }

    MethodNode(
      Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC,
      "callsite",
      "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
      null,
      null
    ).also(classNode.methods::add).instructions.let { insn ->
      insn.add(FieldInsnNode(Opcodes.GETSTATIC, classNode.name, "callsite", "Ljava/lang/invoke/CallSite;"))
      insn.add(InsnNode(Opcodes.ARETURN))
    }

    val classWriter =
      ClassWriterWithClassLoader(ownerClass.classLoader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
    classNode.accept(classWriter)
    val bytes = classWriter.toByteArray()
    val dumpPath = Paths.get("dump/" + classNode.name + ".class")
    dumpPath.parent.createDirectories()
    dumpPath.writeBytes(bytes)

    val executorClass = CodeDefClassLoader(ownerClass.classLoader)
      .define(classNode.name.replace('/', '.'), bytes, 0, bytes.size)

    val methodType = MethodType.fromMethodDescriptorString(rawType.descriptor, classLoader)
    val handle = if (owner == null) {
      JvmHacker.lookup().findStatic(ownerClass, methodName, methodType)
    } else {
      JvmHacker.lookup().findVirtual(ownerClass, methodName, methodType).bindTo(owner)
    }
    return executorClass.getConstructor(CallSite::class.java).newInstance(ConstantCallSite(handle)) as EventExecutor
  }

  fun generateDirectExecutor(
    ownerClass: Class<*>,
    owner: Any?,
    methodName: String,
    methodType: Type,
  ): EventExecutor {
    val ownerInternalName = Type.getInternalName(ownerClass)
    val ownerDescriptor = Type.getDescriptor(ownerClass)
    val classNode = ClassNode()
    classNode.version = Opcodes.V1_8
    classNode.access = Opcodes.ACC_PUBLIC
    classNode.name = ownerInternalName + "\$ankh-event-executor-generated\$" + idAllocator.incrementAndGet()
    classNode.superName = "java/lang/Object"
    classNode.interfaces = listOf("org/bukkit/plugin/EventExecutor")
    if (owner != null) {
      classNode.fields.add(
        FieldNode(
          Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
          "owner",
          ownerDescriptor,
          null,
          null
        )
      )
      MethodNode(
        Opcodes.ACC_PUBLIC,
        "<init>",
        "($ownerDescriptor)V",
        null,
        null
      ).also(classNode.methods::add).instructions.let { insn ->
        insn.add(VarInsnNode(Opcodes.ALOAD, 0))
        insn.add(
          MethodInsnNode(
            Opcodes.INVOKESPECIAL,
            "java/lang/Object",
            "<init>",
            "()V"
          )
        )
        insn.add(VarInsnNode(Opcodes.ALOAD, 0))
        insn.add(VarInsnNode(Opcodes.ALOAD, 1))
        insn.add(FieldInsnNode(Opcodes.PUTFIELD, classNode.name, "owner", ownerDescriptor))
        insn.add(InsnNode(Opcodes.RETURN))
      }
    } else {
      MethodNode(
        Opcodes.ACC_PUBLIC,
        "<init>",
        "()V",
        null,
        null
      ).also(classNode.methods::add).instructions.let { insn ->
        insn.add(VarInsnNode(Opcodes.ALOAD, 0))
        insn.add(
          MethodInsnNode(
            Opcodes.INVOKESPECIAL,
            "java/lang/Object",
            "<init>",
            "()V"
          )
        )
        insn.add(InsnNode(Opcodes.RETURN))
      }
    }

    MethodNode(
      Opcodes.ACC_PUBLIC,
      "execute",
      "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Event;)V",
      null,
      null
    ).also(classNode.methods::add).instructions.let { insn ->
      if (owner != null) {
        insn.add(
          FieldInsnNode(
            Opcodes.GETSTATIC,
            classNode.name,
            "owner",
            ownerDescriptor
          )
        )
      }
      insn.add(VarInsnNode(Opcodes.ALOAD, 2))
      insn.add(TypeInsnNode(Opcodes.CHECKCAST, methodType.argumentTypes[0].internalName))
      insn.add(
        MethodInsnNode(
          if (owner != null) {
            Opcodes.INVOKEVIRTUAL
          } else {
            Opcodes.INVOKESTATIC
          },
          ownerInternalName,
          methodName,
          methodType.descriptor
        )
      )
      insn.add(InsnNode(Opcodes.RETURN))
    }

    val classWriter =
      ClassWriterWithClassLoader(ownerClass.classLoader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
    classNode.accept(classWriter)
    val bytes = classWriter.toByteArray()
    val dumpPath = Paths.get("dump/" + classNode.name + ".class")
    dumpPath.parent.createDirectories()
    dumpPath.writeBytes(bytes)

    val executorClass = CodeDefClassLoader(ownerClass.classLoader)
      .define(classNode.name.replace('/', '.'), bytes, 0, bytes.size)

    return if (owner != null) {
      executorClass.getConstructor(ownerClass).newInstance(owner)
    } else {
      executorClass.getConstructor().newInstance()
    } as EventExecutor
  }

  private class CodeDefClassLoader(parent: ClassLoader) : ClassLoader(parent) {
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
      innerClasses[name]?.let { return it }
      return super.loadClass(name, resolve)
    }

    fun define(name: String, b: ByteArray, off: Int, len: Int): Class<*> {
      return defineClass(name, b, off, len, null)
    }
  }
}