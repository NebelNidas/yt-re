package yt_mappings_plugin.tostring.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

public class FromParamValue extends BasicValue {
    private final int index;

    public FromParamValue(int index, Type type) {
        super(type);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
