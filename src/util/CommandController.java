package util;

import com.google.gson.Gson;
import com.nmn.keystroke.java.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandController {
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Map<Integer, Command> commandMap = new HashMap<>();
    private List<String> commandNames = new ArrayList<>();
    private SystemTask systemTask;
    private int port;
    private boolean isPort;

    public boolean addCommand(Command command, boolean fromUI) {
        command.setId(getNextID());
        commandMap.put(command.getId(), command);
        return saveCommandList(fromUI);
    }

    private int getNextID() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Command c : getCommandList()) {
            list.add(c.getId());
        }
        if (list.size() == 0)
            return 1;
        Collections.sort(list);
        return list.get(list.size() - 1) + 1;
    }

    public Command getCommand(int id) {
        Command command = null;
        if (commandMap.containsKey(id))
            command = commandMap.get(id);
        return command;
    }

    private List<String> getCommandNames(Map<Integer, Command> tempMap) {
        List<String> names = new ArrayList<>();
        for (Command c : tempMap.values()) {
            names.add(c.getName());
        }
        return names;
    }

    public DefaultListModel<Command> getDefaultListModel() {
        DefaultListModel<Command> list = new DefaultListModel<>();

        for (Command c : commandMap.values()) {
            list.addElement(c);
        }
        return list;
    }

    boolean saveCommandList(boolean ui) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream("resources/commands.dat"));
            out.writeObject(commandMap);
            out.close();
            commandNames = sortList(getCommandNames(commandMap));
            if (ui) {
                systemTask.buildPopUpMenu();
            }
            LOG.log(Level.INFO, "Saved commands: " + Arrays.toString(commandNames.toArray()));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to save commands: %names. \n" + Arrays.toString(commandNames.toArray()));
            LOG.log(Level.WARNING, e.getMessage());
            return false;
        }
        systemTask.buildPopUpMenu();
        return true;
    }

    private void loadCommandList() {
        HashMap<Integer, Command> tempMap;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/commands.dat"));
            tempMap = (HashMap) in.readObject();
            commandMap = new HashMap<>(tempMap);
            commandNames = getCommandNames(commandMap);
            String[] arr = commandNames.toArray(new String[0]);
            LOG.log(Level.INFO, "Loaded commands: " + Arrays.toString(arr));
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    private List<String> sortList(List<String> names) {
        Collections.sort(names);
        return names;
    }

    public boolean removeCommand(int id, boolean fromUI) {
        Command result = commandMap.remove(id);
        if (result != null) {
            saveCommandList(fromUI);
            return true;
        } else return false;
    }

    public boolean executeCommand(int id) {
        Command c = getCommand(id);
        if (c == null) {
            LOG.log(Level.WARNING, "ID " + id + " does not exist");
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
            LOG.log(Level.WARNING, "Error executing command " + command.getName());
            LOG.log(Level.WARNING, "Error: " + Arrays.toString(e.getStackTrace()));
            return false;
        }
        LOG.log(Level.INFO, "Successfully executed command " + command.getName() + ": " + command.getCommand());
        return true;
    }

    public String makeCommand(String s) {
        String[] parts = s.split("\\\\");
        StringBuilder resBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            resBuilder.append(parts[i]).append("\\\\");
        }
        String res = resBuilder.toString();
        res += parts[parts.length - 1];
        return res;
    }

    public String unmakeCommand(String s) {
        String[] parts = s.split("\\\\\\\\");
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].equals("")) {
                res.append(parts[i]).append("\\");
            }
        }
        res.append(parts[parts.length - 1]);
        return res.toString();
    }

    public void loadData() {
        loadCommandList();
        loadPort();
        commandNames = sortList(getCommandNames(commandMap));
    }

    private void loadPort() {
        int tempPort;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/port.dat"));
            tempPort = (Integer) in.readObject();
            if (tempPort >= 1 && tempPort <= 65535) {
                port = tempPort;
                isPort = true;
                LOG.log(Level.INFO, "Port loaded successfully");
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load port number");
        }
    }

    public String getCommandListJSON() {
        Gson gson = new Gson();
        String result = gson.toJson(getCommandNames(commandMap).toArray(new String[commandMap.size()]));
        LOG.log(Level.INFO, result);
        return result;
    }

    public String[] getCommandNames() {
        return commandNames.toArray(new String[0]);
    }

    boolean updateCommand(Command command, boolean fromUI) {
        commandMap.put(command.getId(), command);
        return saveCommandList(fromUI);
    }

    void setSystemTask(SystemTask systemTask) {
        this.systemTask = systemTask;
    }

    public boolean isPort() {
        return isPort;
    }

    public int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
        savePort();
    }

    private void savePort() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("resources/port.dat"));
            out.writeObject(port);
            out.close();
            LOG.log(Level.INFO, "Successfully saved port: " + port);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not save port: " + port);
            LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
    }

    private Map<Integer, Command> getCommandMap() {
        return commandMap;
    }

    List<Command> getCommandList() {
        ArrayList<Command> list = new ArrayList<>(getCommandMap().values());
        list.sort(Comparator.comparingInt(Command::getId));
        return list;
    }

    boolean handleKeyPress(String keys) {
        Robot robot;
        KeyStroke stroke;
        try {
            robot = new Robot();
            ExtendedKeyCodes keyCodes = new ExtendedKeyCodes();
            for (char c : keys.toCharArray()) {
                int code = keyCodes.getExtendedKeyCodeForChar(c);
                if (keyCodes.isUpperCase()) {
                    LOG.log(Level.INFO, "Pressing shift");
                    robot.keyPress(KeyEvent.VK_SHIFT);
                }
                LOG.log(Level.INFO, "Pressing key " + c + " with code " + code);
                robot.keyPress(code);
                robot.keyRelease(code);
                if (keyCodes.isUpperCase()) {
                    LOG.log(Level.INFO, "(de)Pressing shift");
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }
            }
        } catch (AWTException e) {
            LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            return false;
        } catch (IllegalArgumentException e){
            LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }
}
