package util;

import com.google.gson.Gson;
import model.Command;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandControllor {
    private List<Command> commandList = new ArrayList<>();
    private List<String> commandNames = new ArrayList<>();
    private final Logger LOG = Logger.getLogger(CommandControllor.class.getName());
    private SystemTask systemTask;
    private int port;
    private boolean isPort;

    public boolean addCommand(Command command, boolean fromUI) {
        for (Command c : commandList) {
            if (c.getName().equals(command.getName())) {
                System.out.println("ERROR: Name already exists. Please use unique name");
                LOG.log(Level.WARNING, "Name already exists");
                return false;
            }
        }
        if (commandList.add(command)) {
            saveCommandList(fromUI);
            return true;
        } else
            return false;
    }

    public Command getCommand(String name) {
        Command command = null;
        for (Command c : commandList) {
            if (c.getName().equals(name))
                command = c;
        }
        return command;
    }

    private List<String> getCommandNames(List<Command> tempList) {
        List<String> names = new ArrayList<>();
        for (Command c : tempList) {
            names.add(c.getName());
        }
        return names;
    }

    public DefaultListModel<String> fillList() {
        DefaultListModel<String> list = new DefaultListModel<>();

        for (Command c : commandList) {
            list.addElement(c.getName());
        }
        return list;
    }

    public void saveCommandList(boolean ui) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("resources/commands.dat"));
            out.writeObject(commandList);
            out.close();
            commandNames = sortList(getCommandNames(commandList));
            if (ui) {
                systemTask.buildPopUpMenu();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "resource"})
    private void loadCommandList() {
        List<Command> tempList;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/commands.dat"));
            tempList = (ArrayList<Command>) in.readObject();
            commandList = new ArrayList<>(tempList);
            commandNames = getCommandNames(commandList);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    private List<String> sortList(List<String> names) {
        Collections.sort(names);
        return names;
    }

    public boolean removeCommand(String name, boolean fromUI) {
        Command c = getCommand(name);
        if (c == null) {
            System.out.println("ERROR: Name does not exist. Could not delete");
            LOG.log(Level.WARNING, "Name already exists. Could not delete");
            return false;
        } else {
            return removeCommand(c, fromUI);
        }
    }

    private boolean removeCommand(Command command, boolean fromUI) {
        boolean result = commandList.remove(command);
        if (result)
            saveCommandList(fromUI);
        return result;
    }

    public boolean executeCommand(String name) {
        Command c = getCommand(name);
        if (c == null) {
            System.out.println("ERROR: Name does not exist. Could not delete");
            LOG.log(Level.WARNING, "Name already exists. Could not delete");
            return false;
        } else {
            return executeCommand(c);
        }
    }

    private boolean executeCommand(Command command) {
        ProcessBuilder process = new ProcessBuilder();
        process.command(command.getCommand(), command.getArgs());
        try {
            process.start();
        } catch (IOException e) {
            System.out.println("Error executing command " + command.getName());
            System.out.println("Error: " + e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String makeCommand(String s) {
        String[] parts = s.split("\\\\");
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            resBuilder.append(parts[i]).append("\\\\"); //TODO: Test automatic stringbuilder impl
        }
        String res = resBuilder.toString();
        res += parts[parts.length - 1];
        return res;
    }

    public String unmakeCommand(String s) {
        String[] parts = s.split("\\\\\\\\");
        String res = "";
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].equals("")) {
                res += parts[i] + "\\";
            }
        }
        res += parts[parts.length - 1];
        return res;
    }

    public void loadData() {
        loadCommandList();
        loadPort();
        commandNames = sortList(getCommandNames(commandList));
    }

    private void loadPort() {
        int tempPort;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/port.dat"));
            tempPort = (Integer) in.readObject();
            if (tempPort >= 1 && tempPort <= 65535) {
                port = tempPort;
                isPort = true;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    public String getCommandListJSON() {
        Gson gson = new Gson();
        return gson.toJson(getCommandNames(commandList).toArray());
    }

    public String[] getCommandNames() {
        return commandNames.toArray(new String[0]);
    }

    private void updateCommand(Command command, boolean fromUI) {
        if (commandList.contains(command)) {
            saveCommandList(fromUI);
        }
    }

    public void setSystemTask(SystemTask systemTask) {
        this.systemTask = systemTask;
    }

    public boolean isPort() {
        return isPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        savePort();
    }

    private void savePort() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("resources/port.dat"));
            out.writeObject(port);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public int getCommandListLength() {
        return 0; // TODO: Find actual length of the message
    }

    public boolean modifyCommand(Command command) {
        if (commandNames.contains(command.getName())) {
            for (Command c : commandList) {
                if (c.getName().equals(command.getName())) {
                    c = command;
                    saveCommandList(false);
                    return true;
                }
            }
        }
        return false;
    }
}
