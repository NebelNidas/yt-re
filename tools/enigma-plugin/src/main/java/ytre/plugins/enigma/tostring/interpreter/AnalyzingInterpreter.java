package ytre.plugins.enigma.tostring.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import cuchaz.enigma.translation.representation.entry.FieldEntry;

public class AnalyzingInterpreter extends BasicInterpreter implements Opcodes {
	private final String owner;
	public BasicValue ret;
	public Map<Integer, FieldEntry> paramToFieldMap = new HashMap<>();

	public AnalyzingInterpreter(String owner) {
		super(ASM9);
		this.owner = owner;
	}

	@Override
	public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
		if (insn instanceof TypeInsnNode typeInsn
				&& typeInsn.getOpcode() == Opcodes.NEW
				&& typeInsn.desc.equals("java/lang/StringBuilder")) {
			return new StringBuilderValue();
		}

		if (insn instanceof LdcInsnNode ldc
				&& ldc.cst instanceof String str) {
			return new ConstantStringValue(str);
		}

		return super.newOperation(insn);
	}

	@Override
	public BasicValue newParameterValue(boolean isInstanceMethod, int local, Type type) {
		if (!isInstanceMethod) throw new IllegalStateException("Trying to analyze static toString");

		if (local == 0) {
			return new ThisValue(owner);
		} else {
			return new FromParamValue(local, type);
		}
	}

	@Override
	public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
		if (insn instanceof FieldInsnNode fieldInsn
				&& fieldInsn.owner.equals(owner)
				&& value instanceof ThisValue) {
			return new FromFieldValue(fieldInsn.name, fieldInsn.desc);
		}

		return super.unaryOperation(insn, value);
	}

	@Override
	public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2) throws AnalyzerException {
		if (insn instanceof FieldInsnNode fieldInsn
				&& fieldInsn.getOpcode() == PUTFIELD
				&& fieldInsn.owner.equals(owner)
				&& value1 instanceof ThisValue
				&& value2 instanceof FromParamValue param) {
			paramToFieldMap.put(param.getIndex(), FieldEntry.parse(fieldInsn.owner, fieldInsn.name, fieldInsn.desc));
		}

		return super.binaryOperation(insn, value1, value2);
	}

	@Override
	public BasicValue naryOperation(AbstractInsnNode insn, List<? extends BasicValue> values) throws AnalyzerException {
		if (insn instanceof MethodInsnNode methodInsn) {
			if (methodInsn.owner.equals("java/lang/StringBuilder")
					&& values.size() > 1
					&& values.get(0) instanceof StringBuilderValue sb) {
				if (values.size() > 2) throw new AnalyzerException(insn, "Couldn't decipher StringBuilder method " + methodInsn.name + methodInsn.desc);

				sb.tokens.add(values.get(1));
				return sb;
			}

			if (methodInsn.owner.equals("java/lang/StringBuilder")
					&& methodInsn.name.equals("toString")) {
				return new StringBuilderValue.Built((StringBuilderValue) values.get(0));
			}

			FromFieldValue fromField = null;
			boolean foundMany = false;

			for (BasicValue val : values) {
				if (val instanceof FromFieldValue f) {
					if (fromField != null) {
						foundMany = true;
						break;
					}

					fromField = f;
				}
			}

			if (!foundMany && fromField != null) {
				return new ProcessedValue(Type.getReturnType(methodInsn.desc), fromField);
			}
		}

		return super.naryOperation(insn, values);
	}

	@Override
	public void returnOperation(AbstractInsnNode insn, BasicValue value, BasicValue expected) {
		ret = value;
	}
}
