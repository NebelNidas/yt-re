package ytre.gitifier.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.tinylog.Logger;
import com.unascribed.flexver.FlexVerComparator;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.impl.NoOpCodeCache;
import jadx.core.utils.files.FileUtils;

import ytre.gitifier.RepoManager;
import ytre.gitifier.VersionBundle;

public class GenState {
	private final Path apkDir;
	private final Path intermediaryDir;
	private final Path outputDir;
	private final File cacheFile;
	private final RepoManager repoManager;
	private Map<String, VersionBundle> versionBundles = new HashMap<>();

	public GenState(Path apkDir, Path intermediaryDir, Path outputDir) throws Exception {
		this.apkDir = apkDir;
		this.intermediaryDir = intermediaryDir;
		this.outputDir = outputDir;
		this.cacheFile = outputDir.resolve("gitter-metadata.properties").toFile();

		repoManager = new RepoManager(outputDir);
	}

	public void generate(boolean overwriteExisting) throws Exception {
		gatherVersionBundles();

		if (overwriteExisting) {
			FileUtils.deleteDirIfExists(outputDir);
		}

		List<VersionBundle> bundles = List.copyOf(versionBundles.values());
		bundles.sort((v1, v2) -> FlexVerComparator.compare(v1.version, v2.version));
		generate(bundles);
	}

	public void update() throws Exception {
		gatherVersionBundles();
		String lastCommited = repoManager.getLastCommitVersion();
	}

	private void generate(List<VersionBundle> versions) throws Exception {
		repoManager.reload();

		for (VersionBundle bundle : versionBundles.values()) {
			Logger.info("Cleaning up output directory...");
			cleanupDir();

			Logger.info("Decompiling version {}...", bundle.version);
			decompile(bundle.version);
			deleteFilesRecursively(".*\\.dex$");

			Logger.info("Committing version {}...", bundle.version);
			repoManager.commit(bundle.version);
		}

		repoManager.close();
	}

	private void gatherVersionBundles() throws Exception {
		List<String> versions = new ArrayList<>();

		try (Stream<Path> stream = Files.list(intermediaryDir)) {
			for (Path path : stream.toList()) {
				if (Files.isDirectory(path)) {
					continue;
				}

				String name = path.getFileName().toString();

				if (!name.endsWith(".tiny") || name.startsWith("counter")) {
					continue;
				}

				String version = name.substring(0, name.lastIndexOf(".tiny"));
				versionBundles.put(version, new VersionBundle(version, null, path));
				versions.add(version);
			}
		}

		try (Stream<Path> stream = Files.list(apkDir)) {
			for (Path path : stream.toList()) {
				if (Files.isDirectory(path)) {
					continue;
				}

				String name = path.getFileName().toString();

				if (!name.startsWith("youtube-") || !name.endsWith(".apk")) {
					continue;
				}

				String version = name.substring(8, name.lastIndexOf(".apk"));
				VersionBundle bundle;

				if ((bundle = versionBundles.get(version)) != null) {
					bundle.apkPath = path;
				}
			}
		}
	}

	private void assertApksArePresent(List<VersionBundle> versionBundles) throws Exception {
		List<String> missingVersions = new ArrayList<>();

		for (VersionBundle bundle : versionBundles) {
			if (bundle.apkPath == null) {
				missingVersions.add(bundle.version);
			}
		}

		if (!missingVersions.isEmpty()) {
			throw new RuntimeException("Missing APK(s) for version(s): " + String.join(",", missingVersions));
		}
	}

	private void cleanupDir() {
		File[] files = outputDir.toFile().listFiles();

		if (files == null) {
			return;
		}

		Arrays.asList(files).stream()
				.filter(file -> !file.getName().equals(".git"))
				.forEach(file -> {
					if (file.isDirectory()) {
						FileUtils.deleteDir(file);
					} else {
						file.delete();
					}
				});
	}

	private void deleteFilesRecursively(String regex) {
		try (Stream<Path> stream = Files.walk(outputDir)) {
			stream.filter(path -> !path.startsWith(outputDir.resolve(".git").toAbsolutePath()))
					.filter(path -> path.getFileName().toString().matches(regex))
					.forEach(path -> {
						if (path.toFile().isDirectory()) {
							FileUtils.deleteDirIfExists(path);
						} else {
							try {
								Files.deleteIfExists(path);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void decompile(String version) {
		JadxArgs jadxArgs = createJadxArgs();
		jadxArgs.setInputFile(apkDir.resolve("youtube-" + version + ".apk").toFile());
		jadxArgs.setUserRenamesMappingsPath(intermediaryDir.resolve(version + ".tiny"));

		try (JadxDecompiler jadx = new JadxDecompiler(jadxArgs)) {
			jadx.load();
			jadx.save();
		}
	}

	private JadxArgs createJadxArgs() {
		JadxArgs args = new JadxArgs();
		args.setOutDir(outputDir.toFile());
		args.setCodeCache(NoOpCodeCache.INSTANCE);
		args.setShowInconsistentCode(true);
		args.setRenameValid(false);
		args.setThreadsCount(Runtime.getRuntime().availableProcessors() / 3);

		return args;
	}
}
