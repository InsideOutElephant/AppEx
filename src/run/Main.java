package run;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JList;

import ui.View;
import util.CommandControllor;
import util.ServerUtil;
import util.SystemTask;

public class Main {
	private CommandControllor cont;
	private JFrame frame;
	private View ui;
	private ServerUtil serverUtil;

	public Main(CommandControllor cont, View ui, ServerUtil serverUtil) {
		this.cont = cont;
		this.ui = ui;
		this.serverUtil = serverUtil;
	}

	public static void main(String[] args) {
		CommandControllor cont = new CommandControllor();
		JFrame frame = new JFrame();
		JList<String> list = new JList<String>();
		ServerUtil serverUtil = new ServerUtil(cont);
		View ui = new View(cont);
		cont.setUI(ui);
		Main main = new Main(cont, ui, serverUtil);
		main.initialize();
	}

	private void initialize() {
		cont.loadData();
		serverUtil.start();
		setOnExit();
//		ui.build();
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String IP = "Error: Could not get IP address";
		try {
			IP = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		SystemTask task = new SystemTask(cont, IP);
		task.createTask();
	}

	private void setOnExit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				serverUtil.setEnabled(false);
			}
		});
	}

}
