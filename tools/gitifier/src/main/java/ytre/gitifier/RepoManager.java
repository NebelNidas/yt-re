package ytre.gitifier;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

public class RepoManager {
	private Git git;
	private Path repoPath;

	public RepoManager(Path repoPath) throws Exception {
		this.repoPath = repoPath;
		reload();
	}

	public void reload() throws Exception {
		this.git = Git.init().setDirectory(repoPath.toFile()).call();
	}

	public void commit(String message) throws Exception {
		git.add().addFilepattern(".").call();
		git.commit().setAll(true).setMessage(message).setAuthor("YouTube", "example@example.com").call();
	}

	public void close() {
		git.close();
	}

	public String getLastCommitVersion() throws Exception {
		RevCommit latestCommit = git.log()
				.setMaxCount(1)
				.call()
				.iterator()
				.next();
		return latestCommit.getShortMessage();
	}
}
