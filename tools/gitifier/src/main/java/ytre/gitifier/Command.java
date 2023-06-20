package ytre.gitifier;

public abstract class Command {
	public final String name;

	public Command(String name) {
		this.name = name;
	}

	public abstract String getHelpString();
	public abstract boolean isArgumentCountValid(int count);
	public abstract void run(String[] args) throws Exception;
}
