package ytre.gitifier;

import java.nio.file.Path;

public class VersionBundle {
	public String version;
	public Path apkPath;
	public Path intermediaryPath;

	public VersionBundle(String version, Path apkPath, Path intermediaryPath) {
		this.version = version;
		this.apkPath = apkPath;
		this.intermediaryPath = intermediaryPath;
	}
}
