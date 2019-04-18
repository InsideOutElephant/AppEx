package model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private Command command;
    private List<Command> commandList;
    private int length;
    private MessageType type;

    public Message() {
    }

    public Message(Command command, int length, MessageType type) {
        this.command = command;
        this.length = length;
        this.type = type;
    }

    public Message(List<Command> commandList, int length, MessageType type) {
        this.commandList = commandList;
        this.length = length;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public MessageType getType() {
        return type;
    }

    public Command getCommand() {
        return command;
    }

    public List<Command> getCommandList() {
        return commandList;
    }
}
