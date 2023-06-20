package ytre.gitifier;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.tinylog.Logger;

import ytre.gitifier.commands.GenerateRepoCommand;

public class Main {
	private static final Map<String, Command> COMMAND_MAP = new TreeMap<>();

	public static void addCommand(Command command) {
		COMMAND_MAP.put(command.name.toLowerCase(Locale.ROOT), command);
	}

	static {
		addCommand(new GenerateRepoCommand());
	}

	public static void main(String[] args) {
		if (args.length == 0
				|| !COMMAND_MAP.containsKey(args[0].toLowerCase(Locale.ROOT))
				|| !COMMAND_MAP.get(args[0].toLowerCase(Locale.ROOT)).isArgumentCountValid(args.length - 1)) {
			if (args.length > 0) {
				Logger.error("Invalid command: {}", args[0]);
			}

			Logger.error("Available commands:");

			for (Command command : COMMAND_MAP.values()) {
				Logger.error("\t{} {}", command.name, command.getHelpString());
			}

			return;
		}

		try {
			String[] argsCommand = new String[args.length - 1];

			if (args.length > 1) {
				System.arraycopy(args, 1, argsCommand, 0, argsCommand.length);
			}

			COMMAND_MAP.get(args[0].toLowerCase(Locale.ROOT)).run(argsCommand);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
