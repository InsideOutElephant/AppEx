package log;


import java.io.IOException;
import java.util.logging.*;

public class MyLogger  {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    static private FileHandler fileHTML;
    static private Formatter formatterHTML;

    static public void setup() {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
//        Logger rootLogger = Logger.*getLogger*("");
//        Handler[] handlers = rootLogger.getHandlers();
//        if (handlers[0] instanceof ConsoleHandler) {
//            rootLogger.removeHandler(handlers[0]);
//        }

        logger.setLevel(Level.ALL);
        try {
            fileTxt = new FileHandler("C:/test/AppEx/log/Logging.txt", 8096, 1, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }
}

