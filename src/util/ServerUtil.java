package util;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ServerUtil extends Thread {
    private int port; // TODO: save port to file and make editable from popup
    private ServerSocket serverSocket;
    private boolean enabled = false;
    private final CommandControllor cont;
    static Logger LOG = Logger.getLogger(ServerUtil.class.getName());
    private boolean isPort;

    public ServerUtil(CommandControllor cont) {
        this.cont = cont;
        this.isPort = false;
    }

    public void run() {
        if (isPort) {
            enabled = true;
            try {
                serverSocket = new ServerSocket(port);
                ExecutorService pool = Executors.newFixedThreadPool(1);
                Socket socket = null;
                while (enabled) {
                    socket = serverSocket.accept();
                    pool.execute(new ProcessData(socket, cont));
                }
                socket.close();
            } catch (Exception e) {

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
