package ytre.plugins.enigma.tostring.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ConstantStringValue extends BasicValue {
	private final String value;

	public ConstantStringValue(String value) {
		super(Type.getType(String.class));
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "constant string: " + value;
	}
}
