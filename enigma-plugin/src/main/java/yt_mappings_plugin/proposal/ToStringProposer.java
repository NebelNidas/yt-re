package yt_mappings_plugin.proposal;

import java.util.Optional;

import cuchaz.enigma.api.service.NameProposalService;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import yt_mappings_plugin.index.ToStringIndex;

public class ToStringProposer implements NameProposalService {
    private final ToStringIndex index;

    public ToStringProposer(ToStringIndex index) {
        this.index = index;
    }

    @Override
    public Optional<String> proposeName(Entry<?> obfEntry, EntryRemapper remapper) {
        if (!(obfEntry instanceof ClassEntry klass)) return Optional.empty();

        return Optional.ofNullable(index.getTranslatedClassName(klass.getFullName()));
    }
}
