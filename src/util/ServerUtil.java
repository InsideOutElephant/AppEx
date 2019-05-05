package util;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ServerUtil extends Thread {
    private int port;
    private ServerSocket serverSocket;
    private boolean enabled = false;
    private final CommandController cont;
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isPort;

    public ServerUtil(CommandController cont) {
        this.cont = cont;
        this.isPort = false;
    }

    public void run() {
        if (isPort) {
            enabled = true;
            try {
                serverSocket = new ServerSocket(port);
                LOG.info("Server started, waiting for connection......");
                ExecutorService pool = Executors.newFixedThreadPool(1);
                Socket socket = null;
                while (enabled) {
                    socket = serverSocket.accept();
                    LOG.info("Connection established");
                    pool.execute(new ProcessData(socket, cont));
                }
               // socket.close();
                LOG.info("Server shut down successfully");
            } catch (Exception e) {
                LOG.warning("Server error: " + e.getStackTrace());
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPort(int port) {
        if (port > 0 && port < 65535) {
            this.port = port;
            isPort = true;
        }
    }
}
