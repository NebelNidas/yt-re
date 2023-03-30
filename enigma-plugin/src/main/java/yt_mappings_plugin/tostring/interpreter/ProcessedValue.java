package yt_mappings_plugin.tostring.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ProcessedValue extends BasicValue {
    private final FromFieldValue field;

    public ProcessedValue(Type type, FromFieldValue field) {
        super(type);
        this.field = field;
    }

    public FromFieldValue getField() {
        return field;
    }

    @Override
    public String toString() {
        return field.toString() + " as " + getType().toString();
    }
}
