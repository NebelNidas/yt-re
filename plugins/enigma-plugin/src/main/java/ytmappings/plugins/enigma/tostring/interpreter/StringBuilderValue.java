package ytmappings.plugins.enigma.tostring.interpreter;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;

// The StringBuilder itself.
public class StringBuilderValue extends BasicValue {
	public List<BasicValue> tokens = new ArrayList<>();

	public StringBuilderValue() {
		super(Type.getType(StringBuilder.class));
	}

	public static class Built extends BasicValue {
		public List<BasicValue> tokens;

		public Built(StringBuilderValue value) {
			super(Type.getType(String.class));
			tokens = new ArrayList<>(value.tokens);
		}
	}
}
