package util;

import com.nmn.keystroke.java.Command;
import com.nmn.keystroke.java.Message;
import com.nmn.keystroke.java.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ProcessData implements Runnable {
    private final Socket socket;
    private final CommandController cont;
    private final Logger LOG = Logger.getLogger(ProcessData.class.getName());
    private boolean enabled;

    public ProcessData(Socket socket, CommandController cont) { // TODO: Finish implementing id in command object and change toa map in the controller
        this.socket = socket;
        this.cont = cont;
        this.enabled = true;
    }

    @Override
    public void run() {
        System.out.println("Inside ProcessData");
        InputStream inputStream;
        OutputStream outputStream;
        ObjectInputStream objectInputStream;
        Message request;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            while (enabled) {
                try {
                    request = (Message) objectInputStream.readObject();

                    LOG.log(Level.INFO, "RECEIVING: " + request.toString());
                    Message response = parseMessage(request); //TODO: add try catch for
                    if (response != null) {
                        out.writeObject(response);
                        out.flush();
                        LOG.log(Level.INFO, "SENDING: " + response.toString());
                    }
                } catch (EOFException eof) {
                    LOG.log(Level.WARNING, "EOF occurred - closing connection");
                    enabled = false;
                 //   handle(eof); //TODO maybe not print this - could be a lot of handling
                } catch (IOException e) {
                    handle(e);
                }catch (ClassNotFoundException e) {
                    handle(e);
                }
            }
        } catch (IOException ioe) {

        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                handle(e);
            }
        }
    }

    private void handle(Exception e) {
        e.printStackTrace();
        LOG.log(Level.WARNING, "IO Exception");
        StringBuilder stack = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace())
            stack.append(element.getClassName()).append(": ").append(element.getMethodName()).append(": ").append(element.getLineNumber()).append("\n");
        LOG.log(Level.WARNING, e.getMessage());
        LOG.log(Level.WARNING, stack.toString());
    }

    private Message parseMessage(Message message) {
        Message response = null;
        Command command;
        boolean result;
        if (message != null) {
            switch (message.getType()) {

                case CREATE:
                    command = message.getCommand();
                    result = cont.addCommand(command, false);
                    response = new Message(MessageType.RESPONSE, result); //TODO: Test creation of
                    break;
                case READ:
                    List<Command> commandList = cont.getCommandList();
                    response = new Message(commandList, MessageType.RESPONSE, true);
                    break;
                case UPDATE:
                    command = message.getCommand();
                    result = cont.updateCommand(command, false);
                    response = new Message(MessageType.RESPONSE, result);
                    break;
                case DELETE:
                    command = message.getCommand();
                    result = cont.removeCommand(command.getId(), false);
                    response = new Message(MessageType.RESPONSE, result);
                    break;
                case EXECUTE:
                    command = message.getCommand();
                    result = cont.executeCommand(command.getId());
                    response = new Message(MessageType.RESPONSE, result);
                    break;
                case END:
                    LOG.log(Level.INFO, "Client closed connection");
                    enabled = false;
                    break;
                case RESPONSE:
                    break;
                case KEY:
                    String keys = message.getKeys();
                    result = cont.handleKeyPress(keys);
                    response = new Message(MessageType.RESPONSE, result);
                    break;
                case PING:
                    LOG.log(Level.INFO, "PING received");
                    response = new Message(MessageType.PING, true);
            }
        }
        return response;
    }

// --Commented out by Inspection START (20/04/2019 23:47):
//    private String executeCommand(String command) {
//        String result = "";
//        if (command.equalsIgnoreCase("GET")) {
//            result = cont.getCommandListJSON();
//        } else if (command.startsWith("EXECUTE")) {
//            String commandName = parseCommand(command);
//            result = Boolean.toString(cont.executeCommand(commandName));
//        } else if (command.equalsIgnoreCase("END")) {
//            enabled = false;
//        } else {
//            System.out.println("Could not parse command");
//            LOG.log(Level.WARNING, "could not parse command");
//        }
//        return result;
//    }
// --Commented out by Inspection STOP (20/04/2019 23:47)

// --Commented out by Inspection START (20/04/2019 23:51):
//    private String parseCommand(String command) {
//        String[] parts = command.split(":");
//        if (parts.length == 2 && parts[0].equals("EXECUTE")) {
//            return parts[1];
//        } else
//            return null;
//    }
// --Commented out by Inspection STOP (20/04/2019 23:51)
}