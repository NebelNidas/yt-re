package yt_mappings_plugin.index;

import java.util.Set;

import org.objectweb.asm.Opcodes;

import cuchaz.enigma.analysis.index.JarIndex;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.classprovider.ClassProvider;

public class JarIndexHolder implements JarIndexerService, Opcodes {
    private JarIndex jarIndex;

    @Override
    public void acceptJar(Set<String> scope, ClassProvider classProvider, JarIndex jarIndex) {
        this.jarIndex = jarIndex;

        // for (String className : scope) {
        //     ClassNode node = classProvider.get(className);
        //     if (node != null) {
        //         this.visitClassNode(node);
        //         this.simpleTypeSingleIndex.visitClassNode(classProvider, node);
        //     }
        // }
    }

    public JarIndex getJarIndex() {
        return this.jarIndex;
    }
}
