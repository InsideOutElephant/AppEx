package run;

import ui.View;
import util.CommandControllor;
import util.ServerUtil;
import util.SystemTask;

import java.net.Inet4Address;
import java.net.UnknownHostException;

class Main {
	private final CommandControllor cont;
	private final ServerUtil serverUtil;

	private Main(CommandControllor cont, ServerUtil serverUtil) {
		this.cont = cont;
		this.serverUtil = serverUtil;
	}

	public static void main(String[] args) {
		CommandControllor cont = new CommandControllor();
		ServerUtil serverUtil = new ServerUtil(cont);
		Main main = new Main(cont, serverUtil);
		main.initialize();
	}

	private void initialize() {
		cont.loadData();
		if(cont.isPort()) serverUtil.setPort(cont.getPort());
		serverUtil.start();
		setOnExit();
		
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
