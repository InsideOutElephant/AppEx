package util;

import com.google.gson.Gson;
import model.Command;
import ui.View;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandControllor {
    View ui;
    List<Command> commandList = new ArrayList<Command>();
    List<String> commandNames = new ArrayList<String>();
    Logger LOG = Logger.getLogger(CommandControllor.class.getName());
    private SystemTask systenTask;

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

    private List<String> getComandNames(List<Command> tempList) {
        List<String> names = new ArrayList<String>();
        for (Command c : tempList) {
            names.add(c.getName());
        }
        return names;
    }

    public DefaultListModel<String> fillList() {
        DefaultListModel<String> list = new DefaultListModel<String>();

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
            commandNames = sortList(getComandNames(commandList));
            if (ui) {
                systenTask.buildPopUpMenu();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "resource"})
    private void loadCommandList() {
        List<Command> tempList = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/commands.dat"));
            tempList = (ArrayList<Command>) in.readObject();
            commandList = new ArrayList<Command>(tempList);
            commandNames = getComandNames(commandList);
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
        String res = "";
        for (int i = 0; i < parts.length - 1; i++) {
            res += parts[i] + "\\\\";
        }
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
        commandNames = sortList(getComandNames(commandList));
    }

    public String getCommandListJSON() {
        Gson gson = new Gson();
        return gson.toJson(getComandNames(commandList).toArray());
    }

    public void setUI(View ui) {
        this.ui = ui;
    }

    public String[] getCommandNames() {
        return commandNames.toArray(new String[0]);
    }

    private void updateCommand(Command command, boolean fromUI) {
        if (commandList.contains(command)) {
//			commandList.remove(command);
            saveCommandList(fromUI);
        }
    }

    public void setSystemTask(SystemTask systemTask) {
        this.systenTask = systemTask;
    }
}
