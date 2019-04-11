package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class ProcessData implements Runnable {
	private final Socket socket;
	private final CommandControllor cont;
	private boolean enabled;
	private final Logger LOG = Logger.getLogger(ProcessData.class.getName());

	public ProcessData(Socket socket, CommandControllor cont) {
		this.socket = socket;
		this.cont = cont;
		enabled = true;
	}

	@Override
	public void run() {
		System.out.println("Inside ProcessData");
		InputStream inStream;
		OutputStream outStream;
		Scanner scan = null;
		PrintWriter writer = null;
		try {
			inStream = socket.getInputStream();
			scan = new Scanner(inStream);
			outStream = socket.getOutputStream();
			writer = new PrintWriter(outStream, true);
			String command;
			while (enabled) {
				if (scan.hasNextLine()) {
					command = scan.nextLine();
					String response = executeCommand(command);
					writer.println(response);
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				Objects.requireNonNull(scan).close();//TODO: check whatsgoing on here
				writer.close();
				socket.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				LOG.log(Level.WARNING, e.getMessage());
			}
		}

	}

	private String executeCommand(String command) {
		String result = "";
		if (command.equalsIgnoreCase("GET")) {
			result = cont.getCommandListJSON();
		} else if (command.startsWith("EXECUTE")) {
			String commandName = parseCommand(command);
			result = Boolean.toString(cont.executeCommand(commandName));
		} else if (command.equalsIgnoreCase("END")) {
			enabled = false;
		} else {
			System.out.println("Could not parse command");
			LOG.log(Level.WARNING, "could not parse command");
		}
		return result;
	}

	private String parseCommand(String command) {
		String[] parts = command.split(":");
		if (parts.length == 2 && parts[0].equals("EXECUTE")) {
			return parts[1];
		} else
			return null;
	}
}