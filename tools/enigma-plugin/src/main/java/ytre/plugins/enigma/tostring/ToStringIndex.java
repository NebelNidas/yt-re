package ytre.plugins.enigma.tostring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import cuchaz.enigma.analysis.index.JarIndex;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.classprovider.ClassProvider;
import cuchaz.enigma.translation.representation.entry.FieldEntry;
import cuchaz.enigma.translation.representation.entry.LocalVariableEntry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;

import ytre.plugins.enigma.tostring.interpreter.AnalyzingInterpreter;
import ytre.plugins.enigma.tostring.interpreter.ConstantStringValue;
import ytre.plugins.enigma.tostring.interpreter.FromFieldValue;
import ytre.plugins.enigma.tostring.interpreter.ProcessedValue;
import ytre.plugins.enigma.tostring.interpreter.StringBuilderValue;

public class ToStringIndex implements JarIndexerService, Opcodes {
	private final Map<String, String> foundClassNames = new HashMap<>();
	private final Map<FieldEntry, String> foundFieldNames = new HashMap<>();
	private final Map<LocalVariableEntry, String> foundParamNames = new HashMap<>();

	public String getTranslatedClassName(String originalClassName) {
		return foundClassNames.get(originalClassName);
	}

	public String getTranslatedFieldName(FieldEntry field) {
		return foundFieldNames.get(field);
	}

	public String getTranslatedParamName(LocalVariableEntry local) {
		return foundParamNames.get(local);
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

		for (MethodNode m : c.methods) {
			if (m.name.equals("toString") && m.desc.equals("()Ljava/lang/String;")) {
				toString = m;
				break;
			}
		}

		if (toString == null) return;
		if (toString.instructions == null) return;
		boolean foundClassName = false;

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
					foundClassName = true;
					break;
				}
			}
		}

		if (!foundClassName) return;

		// Analyze the toString for field names.
		AnalyzingInterpreter toStringInterpreter = new AnalyzingInterpreter(c.name);
		Analyzer<BasicValue> analyzer = new Analyzer<>(toStringInterpreter);

		try {
			analyzer.analyze(c.name, toString);
		} catch (AnalyzerException e) {
			// rip
			return;
		}

		if (!(toStringInterpreter.ret instanceof StringBuilderValue.Built built)) return;

		boolean foundField = false;
		String lastString = null;

		for (BasicValue val : built.tokens) {
			if (val instanceof ProcessedValue processed) {
				val = processed.getField();
			}

			if (val instanceof FromFieldValue field) {
				if (lastString == null || !lastString.endsWith("=")) continue;

				// This is kinda hacky and should probably be replaced with some standard method.
				int lastCharIndex = lastString.length() - 1;

				while (lastCharIndex > 0) {
					char character = lastString.charAt(lastCharIndex - 1);
					if (!Character.isDigit(character) && !Character.isAlphabetic(character)) break;
					lastCharIndex--;
				}

				String mappedName = lastString.substring(lastCharIndex, lastString.length() - 1);
				FieldEntry entry = FieldEntry.parse(c.name, field.getName(), field.getDesc());

				foundFieldNames.put(entry, mappedName);
				foundField = true;
			}

			if (val instanceof ConstantStringValue constant) {
				lastString = constant.getValue();
			} else {
				lastString = null;
			}
		}

		if (!foundField) return;

		// Analyze the constructors for parameters that are used to init the fields.
		for (MethodNode m : c.methods) {
			if (!m.name.equals("<init>")) continue;

			AnalyzingInterpreter initInterpreter = new AnalyzingInterpreter(c.name);
			Analyzer<BasicValue> analyzer2 = new Analyzer<>(initInterpreter);

			try {
				analyzer2.analyze(c.name, m);
			} catch (AnalyzerException e) {
				// rip
				return;
			}

			for (Map.Entry<Integer, FieldEntry> param : initInterpreter.paramToFieldMap.entrySet()) {
				String fieldTranslated = foundFieldNames.get(param.getValue());

				if (fieldTranslated == null) continue;

				LocalVariableEntry entry = new LocalVariableEntry(
						MethodEntry.parse(c.name, "<init>", m.desc),
						param.getKey(),
						"unneeded",
						true,
						"yes");

				foundParamNames.put(entry, fieldTranslated);
			}
		}
	}
}
