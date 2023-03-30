package yt_mappings_plugin.tostring.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ThisValue extends BasicValue {
    public ThisValue(String owner) {
        super(Type.getObjectType(owner));
    }
}
