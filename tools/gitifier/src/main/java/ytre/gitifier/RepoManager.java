package ytre.gitifier;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class RepoManager {
	private Git git;
	private Path repoPath;

	public RepoManager(Path repoPath) {
		this.repoPath = repoPath;
		reload();
	}

	public void reload() {
		try {
			this.git = Git.init().setDirectory(repoPath.toFile()).call();
		} catch (IllegalStateException | GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	public void commit(String message) {
		try {
			git.add().addFilepattern(".").call();
			git.commit().setAll(true).setMessage(message).setAuthor("YouTube", "example@example.com").call();
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		git.close();
	}
}
