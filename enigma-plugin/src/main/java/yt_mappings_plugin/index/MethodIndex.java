package yt_mappings_plugin.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;

public class MethodIndex {
    private final Map<ClassEntry, List<MethodEntry>> methodsByClass = new HashMap<>();

    public void addMethod(MethodEntry methodEntry) {
        methodsByClass.putIfAbsent(methodEntry.getContainingClass(), new ArrayList<>());
        methodsByClass.get(methodEntry.getContainingClass()).add(methodEntry);
    }

    public List<MethodEntry> getMethods(ClassEntry classEntry) {
        return methodsByClass.getOrDefault(classEntry, Collections.emptyList());
    }
}
