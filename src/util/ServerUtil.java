package util;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ServerUtil extends Thread {
	final int PORT = 11223;
	ServerSocket serverSocket;
	private boolean enabled = false;
	private CommandControllor cont;
	static Logger LOG = Logger.getLogger(ServerUtil.class.getName());

	public ServerUtil(CommandControllor cont) {
		this.cont = cont;
	}

	public void run() {

		enabled = true;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			ExecutorService pool = Executors.newFixedThreadPool(1);
			while (enabled) {
				socket = serverSocket.accept();
				pool.execute(new ProcessData(socket, cont));
			}
			socket.close();
		} catch (Exception e) {

		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
