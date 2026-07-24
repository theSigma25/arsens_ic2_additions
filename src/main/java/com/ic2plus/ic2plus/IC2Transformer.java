package com.ic2plus.ic2plus;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class IC2Transformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("ic2.core.item.reactor.ItemReactorMOX")) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("acceptUraniumPulse")) {

                for (AbstractInsnNode insn : method.instructions.toArray()) {

                    if (insn instanceof LdcInsnNode) {
                        LdcInsnNode ldc = (LdcInsnNode) insn;

                        if (ldc.cst instanceof Float && ((Float) ldc.cst) == 4.0F) {
                            ldc.cst = 1.5F;
                        }
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}