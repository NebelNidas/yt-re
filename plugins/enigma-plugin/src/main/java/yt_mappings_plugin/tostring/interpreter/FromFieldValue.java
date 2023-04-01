package yt_mappings_plugin.tostring.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

public class FromFieldValue extends BasicValue {
    private final String name;
    private final String desc;

    public FromFieldValue(String name, String desc) {
        super(Type.getType(desc));
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "own field: " + name + ":" + desc;
    }
}
