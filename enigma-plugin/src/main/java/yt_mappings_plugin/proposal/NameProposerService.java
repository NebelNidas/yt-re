package yt_mappings_plugin.proposal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cuchaz.enigma.api.service.NameProposalService;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import yt_mappings_plugin.index.JarIndexHolder;

public class NameProposerService implements NameProposalService {
    private final List<NameProposer<ClassEntry>> nameProposers;

    public NameProposerService(JarIndexHolder jarIndexHolder) {
        nameProposers = Arrays.asList(new ToStringClassNameExtractor(jarIndexHolder.getJarIndex()));
    }

    @Override
    public Optional<String> proposeName(Entry<?> obfEntry, EntryRemapper remapper) {
        Optional<String> name;

        for (NameProposer<?> proposer : nameProposers) {
            if (proposer.canPropose(obfEntry)) {
                name = proposer.proposeName(obfEntry, remapper);

                if (name.isPresent()) {
                    return name;
                }
            }
        }

        return Optional.empty();
    }
}
