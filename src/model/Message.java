package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private List<Command> commandList;
    private MessageType type;
    private boolean success;

    public Message() {
    }

    public Message(Command command, MessageType type, boolean success) {
        this.commandList = new ArrayList<Command>();
        this.commandList.add(command);
        this.type = type;
        this.success = success;
    }

    public Message(List<Command> commandList, MessageType type, boolean success) {
        this.commandList = commandList;
        this.type = type;
        this.success = success;
    }

    public MessageType getType() {
        return type;
    }

    public Command getCommand() {
        if (commandList.size() > 0)
            return commandList.get(0);
        else return null;
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public boolean isSuccess() {
        return success;
    }
}
