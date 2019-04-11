package model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Command implements Serializable {
	private String name, command, args="";

	public Command(String name, String command) {
		this.name = name;
		this.command = command;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}
