package ytre.plugins.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class YtReGradlePlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		// project.getTasks().register("generateGitRepo", GenerateGitRepoTask.class);
	}
}
