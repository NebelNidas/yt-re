package ytre.plugins.gradle.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class GenerateGitRepoTask extends DefaultTask {
	@InputDirectory
	public abstract DirectoryProperty getApkDir();

	@InputDirectory
	public abstract DirectoryProperty getIntermediaryDir();

	@InputDirectory
	public abstract DirectoryProperty getMappingsDir();

	@TaskAction
	public void run() {
		// Gather all intermediaries
		List<String> versions;

		try (Stream<Path> stream = Files.list(getIntermediaryDir().getAsFile().get().toPath())) {
			versions = stream
					.filter(file -> !Files.isDirectory(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.endsWith(".tiny") && !name.startsWith("counter"))
					.map(name -> name.substring(0, name.lastIndexOf(".tiny") + 1))
					.collect(Collectors.toList());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		System.out.println(versions);
	}
}
