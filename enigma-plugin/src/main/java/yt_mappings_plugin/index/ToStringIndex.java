package yt_mappings_plugin.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import cuchaz.enigma.analysis.index.JarIndex;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.classprovider.ClassProvider;
import org.objectweb.asm.tree.*;

public class ToStringIndex implements JarIndexerService, Opcodes {
    private final Map<String, String> foundClassNames = new HashMap<>();

    public String getTranslatedClassName(String originalClassName) {
        return foundClassNames.get(originalClassName);
    }

    @Override
    public void acceptJar(Set<String> scope, ClassProvider classProvider, JarIndex jarIndex) {
         for (String className : scope) {
             ClassNode node = classProvider.get(className);
             if (node != null) {
                 this.visit(node);
             }
         }
    }

    private void visit(ClassNode c) {
        MethodNode toString = null;
        for (var m : c.methods) {
            if (m.name.equals("toString") && m.desc.equals("()Ljava/lang/String;")) {
                toString = m;
                break;
            }
        }

        if (toString == null) return;
        if (toString.instructions == null) return;

        for (AbstractInsnNode insn : toString.instructions) {
            if (insn instanceof MethodInsnNode call
             && call.getOpcode() == Opcodes.INVOKESPECIAL
             && call.owner.equals("java/lang/StringBuilder")
             && call.name.equals("<init>")
             && call.desc.equals("(Ljava/lang/String;)V")) {
                AbstractInsnNode prev = insn.getPrevious();

                if (!(prev instanceof LdcInsnNode ldc)) continue;

                String firstText = (String) ldc.cst;
                int firstCurly = firstText.indexOf('{');

                if (firstCurly != -1) {
                    String extractedName = firstText.substring(0, firstCurly).strip();

                    if (extractedName.isEmpty()) {
                        // Empty name :when:
                        continue;
                    }

                    foundClassNames.put(c.name, extractedName);
                }
            }
        }
    }
}
