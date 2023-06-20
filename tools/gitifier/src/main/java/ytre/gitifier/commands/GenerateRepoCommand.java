package ytre.gitifier.commands;

import java.io.File;
import java.util.Locale;

import org.tinylog.Logger;

import ytre.gitifier.Command;

public class GenerateRepoCommand extends Command {
	public GenerateRepoCommand() {
		super("generateRepo");
	}

	@Override
	public String getHelpString() {
		return "<apk-dir> <intermediary-dir> <output-dir> [--overwrite]";
	}

	@Override
	public boolean isArgumentCountValid(int count) {
		return count == 3 || count == 4;
	}

	@Override
	public void run(String[] args) throws Exception {
		File apkDir = new File(args[0]);
		File intermediaryDir = new File(args[1]);
		File outputDir = new File(args[2]);
		boolean overwrite = false;

		for (int i = 3; i < args.length; i++) {
			switch (args[i].toLowerCase(Locale.ROOT)) {
				case "--overwrite":
					overwrite = true;
					break;
			}
		}

		if (!apkDir.exists()) {
			throw new IllegalArgumentException("APK directory doesn't exist!");
		}

		if (!intermediaryDir.exists()) {
			throw new IllegalArgumentException("Intermediary directory doesn't exist!");
		}

		if (outputDir.exists() && !overwrite) {
			throw new IllegalArgumentException("Output directory already exists! Pass '--overwrite' if the existing content should be overwritten.");
		}

		GenState state = new GenState(apkDir.toPath(), intermediaryDir.toPath(), outputDir.toPath());

		Logger.info("Generating new Git repo...");
		state.generate(overwrite);
		Logger.info("Done!");
	}
}
