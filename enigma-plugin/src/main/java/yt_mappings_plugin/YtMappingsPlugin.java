package yt_mappings_plugin;

import cuchaz.enigma.api.EnigmaPlugin;
import cuchaz.enigma.api.EnigmaPluginContext;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.api.service.NameProposalService;
import yt_mappings_plugin.index.JarIndexHolder;
import yt_mappings_plugin.proposal.NameProposerService;

public class YtMappingsPlugin implements EnigmaPlugin {
    public static final String SERVICE_ID_PREFIX = "ytmappings:";
    public static final String INDEX_SERVICE_NAME = "jar_index";
    public static final String NAME_PROPOSAL_SERVICE_NAME = "name_proposal";

    @Override
    public void init(EnigmaPluginContext ctx) {
        var indexHolder = new JarIndexHolder();
        ctx.registerService(SERVICE_ID_PREFIX + INDEX_SERVICE_NAME, JarIndexerService.TYPE, ctx1 -> indexHolder);
        ctx.registerService(SERVICE_ID_PREFIX + NAME_PROPOSAL_SERVICE_NAME, NameProposalService.TYPE, ctx1 -> new NameProposerService(indexHolder));
    }
}
