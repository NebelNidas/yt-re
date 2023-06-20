package ytre.gitifier.commands;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.Logger;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.impl.NoOpCodeCache;
import jadx.core.utils.files.FileUtils;

import ytre.gitifier.RepoManager;

public class GenState {
	private final Path apkDir;
	private final Path intermediaryDir;
	private final Path outputDir;
	private final File cacheFile;
	private final RepoManager repoManager;
	private List<String> versions;

	public GenState(Path apkDir, Path intermediaryDir, Path outputDir) {
		this.apkDir = apkDir;
		this.intermediaryDir = intermediaryDir;
		this.outputDir = outputDir;
		this.cacheFile = outputDir.resolve("gitter-metadata.properties").toFile();

		repoManager = new RepoManager(outputDir);
	}

	public void generate(boolean overwriteExisting) {
		gatherVersionsFromIntermediary();
		assertApksArePresent();

		if (overwriteExisting) {
			FileUtils.deleteDirIfExists(outputDir);
		}

		repoManager.reload();

		for (String version : versions) {
			Logger.info("Cleaning up output directory...");
			cleanupFiles();

			Logger.info("Decompiling version {}...", version);
			decompile(version);

			Logger.info("Committing version {}...", version);
			repoManager.commit(version);
		}

		repoManager.close();
	}

	private void gatherVersionsFromIntermediary() {
		try (Stream<Path> stream = Files.list(intermediaryDir)) {
			versions = stream
					.filter(file -> !Files.isDirectory(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.endsWith(".tiny") && !name.startsWith("counter"))
					.map(name -> name.substring(0, name.lastIndexOf(".tiny")))
					.collect(Collectors.toList());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		Logger.info("Intermediary versions: {}", versions);
	}

	private void assertApksArePresent() {
		List<String> apkVersions;

		try (Stream<Path> stream = Files.list(apkDir)) {
			apkVersions = stream
					.filter(file -> !Files.isDirectory(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.startsWith("youtube-") && name.endsWith(".apk"))
					.map(name -> name.substring(8, name.lastIndexOf(".apk")))
					.collect(Collectors.toList());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		List<String> missingVersions = new ArrayList<>();

		Logger.info("APK versions: {}", apkVersions);

		for (String version : versions) {
			if (!apkVersions.contains(version)) {
				missingVersions.add(version);
			}
		}

		if (!missingVersions.isEmpty()) {
			throw new RuntimeException("Missing APK(s) for version(s): " + missingVersions);
		}
	}

	private void cleanupFiles() {
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

	private void decompile(String version) {
		JadxArgs jadxArgs = createJadxArgs();
		jadxArgs.setInputFile(apkDir.resolve("youtube-" + version + ".apk").toFile());
		jadxArgs.setUserRenamesMappingsPath(intermediaryDir.resolve(version + ".tiny"));

		try (JadxDecompiler jadx = new JadxDecompiler(jadxArgs)) {
			jadx.load();
			jadx.save();
		} catch (Throwable e) {
			throw new RuntimeException(e);
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
