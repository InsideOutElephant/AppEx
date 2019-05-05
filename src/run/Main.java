package run;

import log.MyLogger;
import util.CommandController;
import util.ServerUtil;
import util.SystemTask;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Logger;

class Main {
	private final CommandController cont;
	private final ServerUtil serverUtil;
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private Main(CommandController cont, ServerUtil serverUtil) {
		this.cont = cont;
		this.serverUtil = serverUtil;
	}

	public static void main(String[] args) {
		MyLogger.setup();
		CommandController cont = new CommandController();
		ServerUtil serverUtil = new ServerUtil(cont);
		Main main = new Main(cont, serverUtil);
		main.initialize();
	}

	private void initialize() {
		File dir = new File("resources");
		if(!dir.exists()){
			dir.mkdir();
		}
		cont.loadData();
		if(cont.isPort()) serverUtil.setPort(cont.getPort());
		serverUtil.start();
		setOnExit();
		
		String IP = "Error: Could not get IP address";
		try {
            LOG.info("Attempting to retrieve IP");
			IP = Inet4Address.getLocalHost().getHostAddress();
			LOG.info("IP Address: " + IP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LOG.warning(e.getStackTrace().toString());
		}
		SystemTask task = new SystemTask(cont, IP);
		task.createTask();
	}

	private void setOnExit() {
	    LOG.info("Setting shutdown hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            LOG.info("Shutting down server.");
            serverUtil.setEnabled(false);
        }));
	}

}
