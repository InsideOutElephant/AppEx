package util;

import com.nmn.keystroke.java.Command;
import ui.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemTask implements ActionListener {
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final CommandController cont;
    private final String IP;
    private TrayIcon trayIcon = null;
    private MenuItem exitMenuItem;
    private MenuItem addCommandMenuItem;
    private PopupMenu popup;
    private Map<Integer, Menu> popupMenuMap;
    private MenuItem portMenuItem;
    private static final String iconFilePath = "resources/my12.png";

    public SystemTask(CommandController cont, String IP) {
        this.cont = cont;
        this.IP = IP;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void createTask() {
        if (SystemTray.isSupported()) {
            LOG.info("Building system tray task");
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            Image image = Toolkit.getDefaultToolkit().getImage(iconFilePath);
            if (image == null) {
                LOG.warning("No image found in: " + iconFilePath);
            } else {
                LOG.info("Icon loaded");
            }

            //noinspection ConstantConditions,ConstantConditions
            trayIcon = new TrayIcon(image, "Application Launcher");
            buildPopUpMenu();
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
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
        LOG.info("Building popup menu");
        // create a popup menu
        popup = new PopupMenu();
        popupMenuMap = new HashMap<>();
        buildCommandMenuList();
        buildStaticMenuItems();
        trayIcon.setPopupMenu(popup);

    }

    private void buildCommandMenuList() {
        List<Command> commands = cont.getCommandList();
        for (Command c : commands) {
            Menu subMenu = new Menu(c.getName());

//            subMenu.addActionListener(e -> {
//                int id = e.getID();
//                cont.executeCommand(id);
//            });
            MenuItem removeMenuItem = new MenuItem(c.getId() + ": Remove " + c.getName());

            removeMenuItem.addActionListener(e -> {
                String name = e.getActionCommand();
                cont.removeCommand(parseID(name), false);
            });
            MenuItem addArgsMenuItem = new MenuItem(c.getId() + ": Add args to " + c.getName());
            addArgsMenuItem.addActionListener(e -> {
                String name = e.getActionCommand();
                Command command = cont.getCommand(parseID(name));
                String args = getArgs(command);
                if (args == null||args.equals("")) {
                    //TODO: add error handling
                } else {
                    command.setArgs(args);
                    cont.saveCommandList(false);
                }
            });
            MenuItem addIconMenuItem = new MenuItem(c.getId() + ": Add icon to " + c.getName());
            addIconMenuItem.addActionListener(e -> {
                addBase64Image(e.getActionCommand());
            });
            subMenu.add(removeMenuItem);
            subMenu.add(addArgsMenuItem);
            subMenu.add(addIconMenuItem);
            popupMenuMap.put(c.getId(), subMenu);
            popup.add(subMenu);
        }
    }

    private void addBase64Image(String actionCommand) {
        int id = parseID(actionCommand);
        Command command = cont.getCommand(id);
        String imagePath = selectFile("Icon", "ico", "bmp", "jpg", "jpeg", "png", "gif");
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String encodedImage = encodeBase64(image);
        if (encodedImage.equals("")) {
            //TODO: add error handling
        } else {
            command.setBase64Image(encodedImage);
            cont.saveCommandList(false);
        }
    }

    private String getIcon(String path) {
        File file = new File(path);
        ImageIcon icon;
        icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);

        Image image = icon.getImage();
        return encodeBase64(image);
    }

    private String encodeBase64(Image image) {
        String encodedImage = "";
        try {
            BufferedImage bufImg = toBufferedImage(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufImg, "png", baos);
            byte[] bytes = baos.toByteArray();
//            FileInputStream fileInputStreamReader = new FileInputStream(bufImg);
//            byte[] bytes = new byte[(int)image.];
//            fileInputStreamReader.read(bytes);
            encodedImage = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encodedImage;

//        BufferedImage bImage = SwingFXUtils.fromFXImage(logo.getImagePath(), null);
//        ByteArrayOutputStream s = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", s);
//        byte[] res  = s.toByteArray()
//        s.close();
//        Base64.encode(res);

    }

    private String getImagePath() {
        JFileChooser chooser = new JFileChooser();
//        chooser.setFileFilter(new FileNameExtensionFilter("Icon", "ico", "bmp", "jpg","jpeg","png","gif"));
        String imagePath = null;
        int returnVal = chooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            imagePath = chooser.getSelectedFile().getAbsolutePath();
        }
        return imagePath;
    }

    private int parseID(String name) {
        String[] parts = name.split(":");
        return Integer.parseInt(parts[0]);
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
            addCommand();
        } else if (o.equals(exitMenuItem)) {
            System.exit(0);
        } else if (o.equals(portMenuItem)) {
            getPortInput();
        }
    }

    private void addCommand() {
        String filePath = selectFile("exe", "exe");
        Command command = parsePath(filePath);
        String encodedImage = getIcon(filePath);
        if (command != null) {
            if (encodedImage != null)
                command.setBase64Image(encodedImage);
            if (cont.addCommand(command, false))
                buildPopUpMenu();
        }
    }

    private void getPortInput() {
        int parsedInput;
        String input = JOptionPane.showInputDialog("Please enter port number", cont.getPort());
        try {
            parsedInput = Integer.parseInt(input);
            savePort(parsedInput);
        } catch (Exception e) {
            if (input != null && !input.equals(""))
                trayIcon.displayMessage("Incorrect Port", "Please select a numerical value between 1 and 65535", TrayIcon.MessageType.ERROR);
        }

    }

    private void savePort(int port) {
        if (port >= 1 && port <= 65535)
            cont.setPort(port);
        else {
            trayIcon.displayMessage("Incorrect Port", "Please select a numerical value between 1 and 65535", TrayIcon.MessageType.ERROR);
        }
    }

    private String selectFile(String desc, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(desc, extensions));
        String path = "";
        int returnVal = chooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getAbsolutePath();
        }
        return path;
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
            LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
        if (allAttrs == null) return "";
        for (String att : allAttrs) {
            System.out.println("att = " + att);
        }
        return "";
    }

}
