package util;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Command;
import ui.View;

public class SystemTask implements ActionListener {
	SystemTray tray;
	TrayIcon trayIcon = null;
	MenuItem defaultItem;
	MenuItem exitMenuItem;
	MenuItem addCommandMenuItem;
	CommandControllor cont;
	String IP;
	PopupMenu popup;
	Map<String, PopupMenu> popupMenuMap;
	Map<String, MenuItem> subMenuMap;
	boolean initialBuild;
	Image image;

	public SystemTask(CommandControllor cont, String IP) {
		this.cont = cont;
		this.IP = IP;
	}

	public void createTask() {
		initialBuild = true;
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			tray = SystemTray.getSystemTray();
			// load an image
			image = Toolkit.getDefaultToolkit().getImage("resources/my12.png");

			buildPopUpMenu();

			trayIcon.displayMessage("IP Address", IP, TrayIcon.MessageType.INFO);
			// ...
		} else {
			// disable tray option in your application or
			// perform other actions

		}
	}

	private void buildPopUpMenu() {
		// create a popup menu
		popup = new PopupMenu();
		popupMenuMap = new HashMap<String, PopupMenu>();
		subMenuMap = new HashMap<String, MenuItem>();
		buildCommandMenuList();
		buildMenuList();
		trayIcon = new TrayIcon(image, "Application Launcher") FIX MENU REFRESH - CURRENTLY IT CREATES A NEW TASK ICON FOR EACH REFRESH
		trayIcon.setPopupMenu(popup);
		// set the TrayIcon properties
		trayIcon.addActionListener(this);

		// add the tray image
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println(e);
		}
	}

	public void buildCommandMenuList() {
		String[] commands = cont.getCommandNames();
		for (String c : commands) {
			PopupMenu subMenu = new PopupMenu(c);
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
					cont.removeCommand(parseName(name));
				}
			});
			MenuItem addArgsMenuItem = new MenuItem("Add args to " + c);
			addArgsMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					Command command = cont.getCommand(parseName(name));
					String args = getArgs();
					if (args != "" && args != null) {
						command.setArgs(args);
						cont.saveCommandList();
					}
				}
			});
			subMenu.add(removeMenuItem);
			subMenu.add(addArgsMenuItem);
			subMenuMap.put(c, subMenu);
			popupMenuMap.put(c, subMenu);
			popup.add(subMenu);
		}
	}

	protected String parseName(String name) {
		String[] parts = name.split(" ");
		return parts[parts.length - 1];
	}

	public void buildMenuList() {
		if (initialBuild) {
			exitMenuItem = new MenuItem("Exit");
			exitMenuItem.addActionListener(this);
			addCommandMenuItem = new MenuItem("Add application");
			addCommandMenuItem.addActionListener(this);

			popup.add(addCommandMenuItem);
			popup.add(exitMenuItem);
			initialBuild = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(trayIcon)) {
			View ui = new View(cont);
			ui.build();
			ui.getFrame().setVisible(true);
			ui.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		else if (o.equals(addCommandMenuItem)) {
			Command command = selectFile();
			if (command != null) {
				cont.addCommand(command);
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
		chooser.setFileFilter(new FileNameExtensionFilter("exe", "exe"));
		String commandPath = "";
		int returnVal = chooser.showOpenDialog(new JPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			commandPath = chooser.getSelectedFile().getAbsolutePath();
		}
		return parsePath(commandPath);
	}

	private Command parsePath(String commandPath) {
		Command result = null;
		if (commandPath.endsWith(".exe")) {
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

	protected String getArgs() {
		String result = "";
		result = JOptionPane.showInputDialog("Please enter arguments");
		// if(result=="")
		return result;
	}
}
