package yt_mappings_plugin.proposal;

import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.representation.entry.Entry;

import java.util.Optional;

public interface NameProposer<E extends Entry<?>> {
    Optional<String> doProposeName(E entry, EntryRemapper remapper);

    boolean canPropose(Entry<?> entry);

    E upcast(Entry<?> entry);

    default Optional<String> proposeName(Entry<?> entry, EntryRemapper remapper) {
        return doProposeName(upcast(entry), remapper);
    }
}
