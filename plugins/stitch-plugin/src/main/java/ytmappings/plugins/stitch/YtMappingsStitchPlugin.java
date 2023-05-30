package ytmappings.plugins.stitch;

import java.util.regex.Pattern;

import net.fabricmc.stitch.plugin.StitchPlugin;
import net.fabricmc.stitch.representation.ClassStorage;
import net.fabricmc.stitch.representation.JarClassEntry;
import net.fabricmc.stitch.representation.JarFieldEntry;
import net.fabricmc.stitch.representation.JarMethodEntry;

public class YtMappingsStitchPlugin implements StitchPlugin {
	// YT classes without a package are obfuscated.
	private static Pattern classObfuscationPattern = Pattern.compile("^[^/]*$");

	@Override
	public int needsIntermediaryName(ClassStorage storage, JarClassEntry cls) {
		return StitchPlugin.super.needsIntermediaryName(storage, cls) > 0
				&& classObfuscationPattern.matcher(cls.getFullyQualifiedName()).matches() ? 2 : -2;
	}

	@Override
	public int needsIntermediaryName(ClassStorage storage, JarClassEntry cls, JarFieldEntry fld) {
		return StitchPlugin.super.needsIntermediaryName(storage, cls, fld) > 0
				&& needsIntermediaryName(storage, cls) > 0 ? 2 : -2;
	}

	@Override
	public int needsIntermediaryName(ClassStorage storage, JarClassEntry cls, JarMethodEntry mth) {
		return StitchPlugin.super.needsIntermediaryName(storage, cls, mth) > 0
				&& needsIntermediaryName(storage, cls) > 0 ? 2 : -2;
	}
}
