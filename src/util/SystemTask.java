package util;

import model.Command;
import ui.View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemTask implements ActionListener {
    private SystemTray tray;
    private TrayIcon trayIcon = null;
    private MenuItem exitMenuItem;
    private MenuItem addCommandMenuItem;
    private final CommandControllor cont;
    private final String IP;
    private PopupMenu popup;
    private Map<String, Menu> popupMenuMap;
    private Image image;
    private MenuItem portMenuItem;

    public SystemTask(CommandControllor cont, String IP) {
        this.cont = cont;
        this.IP = IP;
    }

    public void createTask() {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            tray = SystemTray.getSystemTray();
            // load an image
            image = Toolkit.getDefaultToolkit().getImage("resources/my12.png");

            trayIcon = new TrayIcon(image, "Application Launcher");
            buildPopUpMenu();
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
                System.exit(1);
            }
            if (cont.isPort())
                trayIcon.displayMessage("IP Address : Port", IP + ":" + cont.getPort(), TrayIcon.MessageType.INFO);
            else {
                trayIcon.displayMessage("Please set a port", "Port needed to start listening for commands", TrayIcon.MessageType.WARNING);
            }
            // ...
            // set the TrayIcon properties
            trayIcon.addActionListener(this);

            setContSystemTask();
        }
    }

    private void setContSystemTask() {
        cont.setSystemTask(this);
    }

    public void buildPopUpMenu() {
        // create a popup menu
        popup = new PopupMenu();
        popupMenuMap = new HashMap<>();
        buildCommandMenuList();
        buildStaticMenuItems();
        trayIcon.setPopupMenu(popup);

    }

    private void buildCommandMenuList() {
        String[] commands = cont.getCommandNames();
        for (String c : commands) {
            Menu subMenu = new Menu(c);
            subMenu.addActionListener(e -> {
                String name = e.getActionCommand();
                cont.executeCommand(name);
            });
            MenuItem removeMenuItem = new MenuItem("Remove " + c);
            removeMenuItem.addActionListener(e -> {
                String name = e.getActionCommand();
                if (cont.removeCommand(parseName(name), false)) {
                    buildPopUpMenu();
                }
            });
            MenuItem addArgsMenuItem = new MenuItem("Add args to " + c);
            addArgsMenuItem.addActionListener(e -> {
                String name = e.getActionCommand();
                Command command = cont.getCommand(parseName(name));
                String args = getArgs(command);
                if (args.equals("") || args == null) {
                    //TODO: add error handling
                } else {
                    command.setArgs(args);
                    cont.saveCommandList(false);
                }
            });
            subMenu.add(removeMenuItem);
            subMenu.add(addArgsMenuItem);
            popupMenuMap.put(c, subMenu);
            popup.add(subMenu);
        }
    }

    private String parseName(String name) {
        String[] parts = name.split(" ");
        return parts[parts.length - 1];
    }

    private void buildStaticMenuItems() {
        exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(this);
        addCommandMenuItem = new MenuItem("Add application");
        addCommandMenuItem.addActionListener(this);
        portMenuItem = new MenuItem("Set Port");
        portMenuItem.addActionListener(this);

        popup.addSeparator();

        popup.add(portMenuItem);
        popup.add(addCommandMenuItem);
        popup.add(exitMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(trayIcon)) {
            View ui = new View(cont);
            ui.build();
            ui.getFrame().setVisible(true);
            ui.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else if (o.equals(addCommandMenuItem)) {
            Command command = selectFile();
            if (command != null) {
                if (cont.addCommand(command, false))
                    buildPopUpMenu();
            }
        }

        // else if(o.equals(removeCommand))

        else if (o.equals(exitMenuItem)) {
            System.exit(0);
        } else if (o.equals(portMenuItem)) {
            getPortInput();
        }
    }

    private void getPortInput() {
        int parsedInput;
        String input = JOptionPane.showInputDialog("Please enter port number", cont.getPort());
        try {
            parsedInput = Integer.parseInt(input);
            savePort(parsedInput);
        } catch (Exception e) {
            trayIcon.displayMessage("Incorrect Port", "Please select a numerical value between 1 and 65535", TrayIcon.MessageType.ERROR);        }

    }

    private void savePort(int port) {
        if (port >= 1 && port <= 65535)
            cont.setPort(port);
        else {
            trayIcon.displayMessage("Incorrect Port", "Please select a numerical value between 1 and 65535", TrayIcon.MessageType.ERROR);
        }
    }

    private Command selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(".exe", "exe"));
        String commandPath = "";
        int returnVal = chooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            commandPath = chooser.getSelectedFile().getAbsolutePath();
        }
        return parsePath(commandPath);
    }

    private Command parsePath(String commandPath) {
        Command result = null;
        if (commandPath.endsWith(".exe") || commandPath.endsWith(".EXE")) {
            String[] parts = commandPath.split("\\\\");
            String name = parts[parts.length - 1];
            name = name.substring(0, name.length() - 4);
            String[] cars = name.split("");
            String newChar = cars[0].toUpperCase();
            name = name.replaceFirst(cars[0], newChar);
            result = new Command(name, cont.makeCommand(commandPath));
        }
        return result;
    }

    private String getArgs(Command command) {
        String result;
        result = JOptionPane.showInputDialog("Please enter arguments", command.getArgs());
        return result;
    }

    private String getFileName(String exePath) {
        Path path = Paths.get("someImage.jpg").toAbsolutePath();

        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        List<String> allAttrs = null;
        try {
            allAttrs = fileAttributeView.list();
        } catch (Exception e) {
        }
        if (allAttrs == null) return "";
        for (String att : allAttrs) {
            System.out.println("att = " + att);
        }
        return "";
    }

}
