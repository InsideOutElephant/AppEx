package com.nmn.keystroke.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 4282155944147152641L;
    private List<Command> commandList;
    private MessageType type;
    private String keys;
    private boolean success;

    public Message() {
    }

    public Message(Command command, MessageType type) {
        if (command == null) {
            this.commandList = new ArrayList<>();
        } else {
            this.commandList = new ArrayList<Command>();
            this.commandList.add(command);
        }
        this.type = type;
        this.success = false;
    }

    public Message(MessageType type, boolean success) {
        this.type = type;
        this.success = success;
        commandList = new ArrayList<>();
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

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("\n{\n\tType:" + type.name() + ", \n\tCommandList:[");
        if (commandList.isEmpty()) {
            result.append("],");
        } else {
            for (Command c : commandList) {
                result.append("\n\t\t{");
                result.append("ID: ").append(c.getId()).append(", Name:").append(c.getName()).append(", Command:").append(c.getCommand()).append(", Args:").append(c.getArgs());
                result.append("}");
            }
            result.append("], ");
        }
        result.append("\n\tKeys:").append(keys);
        result.append("\n\tSuccess:").append(String.valueOf(success).toUpperCase()).append("\n}");
        return result.toString();
    }
}
