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
    SystemTray tray;
    TrayIcon trayIcon = null;
    MenuItem defaultItem;
    MenuItem exitMenuItem;
    MenuItem addCommandMenuItem;
    CommandControllor cont;
    String IP;
    PopupMenu popup;
    Map<String, Menu> popupMenuMap;
    //	List<MenuItem> subMenuList;
    Image image;

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

            trayIcon.displayMessage("IP Address", IP, TrayIcon.MessageType.INFO);
            // ...
            // set the TrayIcon properties
            trayIcon.addActionListener(this);

            setSystsemTask();
        } else {
            // disable tray option in your application or
            // perform other actions

        }
    }

    private void setSystsemTask() {
        cont.setSystemTask(this);
    }

    public void buildPopUpMenu() {
        // create a popup menu
        popup = new PopupMenu();
        popupMenuMap = new HashMap<String, Menu>();
//		subMenuList = new ArrayList<MenuItem>();
        buildCommandMenuList();
        buildMenuList();
        trayIcon.setPopupMenu(popup);

    }

    private void buildCommandMenuList() {
        String[] commands = cont.getCommandNames();
        for (String c : commands) {
            Menu subMenu = new Menu(c);
            subMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = e.getActionCommand();
                    cont.executeCommand(name);
                }
            });
            MenuItem removeMenuItem = new MenuItem("Remove " + c);
            removeMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = e.getActionCommand();
                    if (cont.removeCommand(parseName(name), false)) {
                        buildPopUpMenu();
                    }
                }
            });
            MenuItem addArgsMenuItem = new MenuItem("Add args to " + c);
            addArgsMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = e.getActionCommand();
                    Command command = cont.getCommand(parseName(name));
                    String args = getArgs(command);
                    if (args == "" || args == null) {
                        //TODO: add error handling
                    } else {
                        command.setArgs(args);
                        cont.saveCommandList(false);
                    }
                }
            });
            subMenu.add(removeMenuItem);
            subMenu.add(addArgsMenuItem);
//			subMenuList.add(subMenu);
            popupMenuMap.put(c, subMenu);
            popup.add(subMenu);
        }
    }

    protected String parseName(String name) {
        String[] parts = name.split(" ");
        return parts[parts.length - 1];
    }

    private void buildMenuList() {
        exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(this);
        addCommandMenuItem = new MenuItem("Add application");
        addCommandMenuItem.addActionListener(this);

        popup.addSeparator();
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

    protected String getArgs(Command command) {
        String result;
        result = JOptionPane.showInputDialog("Please enter arguments", command.getArgs());
        // if(result=="")
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

        for (String att : allAttrs) {
            System.out.println("att = " + att);
        }
        return "";
    }

}
