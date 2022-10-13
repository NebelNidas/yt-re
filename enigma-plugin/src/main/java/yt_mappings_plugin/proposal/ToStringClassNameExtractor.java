package yt_mappings_plugin.proposal;

import java.util.Optional;
import java.util.regex.Pattern;

import cuchaz.enigma.analysis.index.JarIndex;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;

public class ToStringClassNameExtractor implements NameProposer<ClassEntry> {
    Pattern returnClassName = Pattern.compile("return \"(\\w+)\\{");
    JarIndex jarIndex;
    MethodEntry currentToStringMethod;

    public ToStringClassNameExtractor(JarIndex jarIndex) {
        this.jarIndex = jarIndex;
    }

    @Override
    public Optional<String> doProposeName(ClassEntry classEntry, EntryRemapper remapper) {
        // TODO: see below
        return null;
    }

    @Override
    public boolean canPropose(Entry<?> entry) {
        if (!(entry instanceof ClassEntry)) {
            return false;
        }

        ClassEntry classEntry = ((ClassEntry) entry);
        findToStringMethod(classEntry);

        // TODO: get access to decompiled code and check for `returnClassName` pattern, then extract the class name

        return false;
    }

    @Override
    public ClassEntry upcast(Entry<?> entry) {
        if (entry instanceof ClassEntry) {
            findToStringMethod((ClassEntry) entry);
        }

        return null;
    }

    private void findToStringMethod(ClassEntry classEntry) {
        currentToStringMethod = jarIndex.getEntryIndex().getMethods()
                .stream()
                .filter(methodEntry -> methodEntry.getContainingClass() == classEntry)
                .filter(methodEntry -> methodEntry.getDesc().equals("toString()Ljava/lang/String"))
                .findFirst().get();
    }
}
