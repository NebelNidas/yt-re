package yt_mappings_plugin;

import cuchaz.enigma.api.EnigmaPlugin;
import cuchaz.enigma.api.EnigmaPluginContext;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.api.service.NameProposalService;
import yt_mappings_plugin.index.ToStringIndex;
import yt_mappings_plugin.proposal.ToStringProposer;

public class YtMappingsPlugin implements EnigmaPlugin {
    public static final String SERVICE_ID_PREFIX = "ytmappings:";
    public static final String TO_STRING_INDEX_ID = "to_string_index";
    public static final String TO_STRING_PROPOSER_ID = "to_string_proposer";

    @Override
    public void init(EnigmaPluginContext ctx) {
        var toStringIndex = new ToStringIndex();
        ctx.registerService(SERVICE_ID_PREFIX + TO_STRING_INDEX_ID, JarIndexerService.TYPE, ctx1 -> toStringIndex);
        ctx.registerService(SERVICE_ID_PREFIX + TO_STRING_PROPOSER_ID, NameProposalService.TYPE, ctx1 -> new ToStringProposer(toStringIndex));
    }
}
