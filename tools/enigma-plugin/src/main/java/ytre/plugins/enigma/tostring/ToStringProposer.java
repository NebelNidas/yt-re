package ytre.plugins.enigma.tostring;

import java.util.Optional;

import cuchaz.enigma.api.service.NameProposalService;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import cuchaz.enigma.translation.representation.entry.FieldEntry;
import cuchaz.enigma.translation.representation.entry.LocalVariableEntry;

public class ToStringProposer implements NameProposalService {
	private final ToStringIndex index;

	public ToStringProposer(ToStringIndex index) {
		this.index = index;
	}

	@Override
	public Optional<String> proposeName(Entry<?> obfEntry, EntryRemapper remapper) {
		if (obfEntry instanceof ClassEntry klass) {
			return Optional.ofNullable(index.getTranslatedClassName(klass.getFullName()));
		} else if (obfEntry instanceof FieldEntry field) {
			return Optional.ofNullable(index.getTranslatedFieldName(field));
		} else if (obfEntry instanceof LocalVariableEntry local) {
			return Optional.ofNullable(index.getTranslatedParamName(local));
		}

		return Optional.empty();
	}
}
