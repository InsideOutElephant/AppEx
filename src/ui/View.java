package ui;

import com.nmn.keystroke.java.Command;
import util.CommandController;
import util.CommandRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class View {
    private static final Map<String, JTextField> uiMap = new HashMap<>();
    private final CommandController cont;
    private final JFrame frame;
    private final JList<Command> list;
    private JTextField commandTF;
    private JTextField argsTF;
    private JTextField nameTF;

    public View(CommandController cont) {
        this.cont = cont;
        this.frame = new JFrame();
        this.list = new JList<>();
    }

    /**
     * Create the application.
     */

    /**
     * Initialize the contents of the frame.
     */
    public void build() {
        this.commandTF = new JTextField();
        this.argsTF = new JTextField();
        this.nameTF = new JTextField();

        uiMap.put("commandTF", commandTF);
        uiMap.put("argsTF", argsTF);
        uiMap.put("nameTF", nameTF);

        frame.setTitle("Application Executer");
        frame.setBounds(100, 100, 500, 400);
        frame.getContentPane().setLayout(null);

        listPanel();
        navPanel();
        displayPanel();
    }


    private void displayPanel() {
        JPanel displayPanel = new JPanel();
        displayPanel
                .setBorder(new TitledBorder(null, "NEW COMMAND", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        displayPanel.setBounds(156, 72, 318, 265);
        frame.getContentPane().add(displayPanel);
        displayPanel.setLayout(null);

        commandTF = new JTextField();
        commandTF.setFont(new Font("Tahoma", Font.PLAIN, 13));
        commandTF.setBounds(10, 137, 263, 31);
        displayPanel.add(commandTF);
        commandTF.setColumns(10);

        argsTF = new JTextField();
        argsTF.setFont(new Font("Tahoma", Font.PLAIN, 13));
        argsTF.setColumns(10);
        argsTF.setBounds(10, 221, 298, 31);
        displayPanel.add(argsTF);

        nameTF = new JTextField();
        nameTF.setFont(new Font("Tahoma", Font.PLAIN, 13));
        nameTF.setColumns(10);
        nameTF.setBounds(10, 53, 298, 31);
        displayPanel.add(nameTF);

        JLabel argsLabel = new JLabel("Arguments:");
        argsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        argsLabel.setBounds(10, 189, 71, 21);
        displayPanel.add(argsLabel);

        JLabel commandLabel = new JLabel("Command:");
        commandLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        commandLabel.setBounds(10, 105, 71, 21);
        displayPanel.add(commandLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        nameLabel.setBounds(10, 21, 71, 21);
        displayPanel.add(nameLabel);

        JButton openBut = new JButton("...");
        openBut.setBounds(283, 137, 25, 31);
        displayPanel.add(openBut);
        openBut.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(displayPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                commandTF.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

    }

    private void navPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setBounds(0, 11, 484, 53);
        frame.getContentPane().add(navPanel);
        navPanel.setLayout(null);

        JButton runBut = new JButton("Execute");
        runBut.addActionListener(arg0 -> cont.executeCommand(list.getSelectedIndex()));
        runBut.setBounds(23, 0, 130, 53);
        navPanel.add(runBut);

        JButton removeBut = new JButton("Remove");
        removeBut.addActionListener(e -> {
            cont.removeCommand(list.getSelectedIndex(), true);
            refreshList();
            clearFields();

        });
        removeBut.setBounds(329, 0, 130, 53);
        navPanel.add(removeBut);

        JButton saveBut = new JButton("Save");
        saveBut.addActionListener(arg0 -> {
            Command command = new Command(nameTF.getText(), cont.makeCommand(commandTF.getText()));
            if (!argsTF.getText().isEmpty())
                command.setArgs(argsTF.getText());
            cont.addCommand(command, true);
            clearFields();
            refreshList();
        });
        saveBut.setBounds(176, 0, 130, 53);
        navPanel.add(saveBut);
    }

    private void clearFields() {
        commandTF.setText("");
        argsTF.setText("");
        nameTF.setText("");
    }

	private void listPanel() {
		JPanel listPanel = new JPanel();
		listPanel.setBorder(new TitledBorder(null, "COMMANDS", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listPanel.setBounds(10, 72, 136, 265);
		frame.getContentPane().add(listPanel);
		listPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 15, 116, 245);
		listPanel.add(scrollPane);

        list.setBounds(0, 0, 1, 1);
        list.setCellRenderer(new CommandRenderer());
        refreshList();
        scrollPane.setViewportView(list);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (list.getModel().getSize() > 0) {
                    int target = list.getSelectedIndex();
                    Command c = cont.getCommand(target);
                    c.setCommand(cont.unmakeCommand(c.getCommand()));
                    fillDetails(c);
                    System.out.println("Command after selection: " + commandTF.getText());
                }
            }
        });
    }

    private void fillDetails(Command command) {
        if (command != null) {
            nameTF.setText(command.getName());
            argsTF.setText(command.getArgs());
            commandTF.setText(command.getCommand());
        }
    }

    private void refreshList() {
        if (cont.getDefaultListModel() != null)
            list.setModel(cont.getDefaultListModel());
    }

// --Commented out by Inspection START (10/04/2019 23:50):
//	public Map<String, JTextField> getUIMap() {
//		return uiMap;
//	}
// --Commented out by Inspection STOP (10/04/2019 23:50)

    public JFrame getFrame() {
        return frame;
    }
}
