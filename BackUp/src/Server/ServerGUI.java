package Server;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ServerGUI {

	static Server s = null;
	JFrame frame;
	JTextField port;
	JButton run;
	Tray t;
	JCheckBox check;
	JPanel runAlways;
	
	public ServerGUI(){
		final JPanel panel = setUpFrame();
		
		final JPanel panelServer = definePortStructure();
		

		addRun(panelServer);
		
		String p = port.getText();
		runServer(Integer.parseInt(p));
		
		panel.add(panelServer);
		panel.add(runAlways);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Image/server.png")));
		
		frame.add(panel);
		frame.setVisible(true);
	}

	public ServerGUI(int port) {
		final JPanel panel = setUpFrame();
		
		final JPanel panelServer = definePortStructure();
		

		addRun(panelServer);
		
		runServer(port);
		check.doClick();
		run.doClick();
		
		panel.add(panelServer);
		panel.add(runAlways);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Image/server.png")));
		
		frame.add(panel);
		exitOrHide();
	}

	private void runServer(final int p) {
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(run.getText().equals("Run")){
					s = new Server(p);
					s.start();
					if(check.isSelected()){
						try {
							FileWriter writer = new FileWriter(new File("boot.txt"));
							writer.write("%In this file the port of the server program is written so the server can start at boot.\n"
									+ "Port: " + p);
							writer.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else{
						try {
							FileWriter writer = new FileWriter(new File("boot.txt"));
							writer.write("%In this file the port of the server program is written so the server can start at boot.\n"
									+ "Port: ");
							writer.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					port.setEnabled(false);
					check.setEnabled(false);
					run.setText("Stop");
				}
				else{
					s.stopActivity();
					run.setText("Run");
					port.setEnabled(true);
					check.setEnabled(true);
				}

			}
		});
	}

	private void addRun(final JPanel panelServer) {
		run = new JButton("Run");
		panelServer.add(run);
		
		
		
		runAlways = new JPanel();
		runAlways.setLayout(new BoxLayout(runAlways, BoxLayout.LINE_AXIS));
		
		check = new JCheckBox("Run on boot");
		runAlways.add(check);
	}

	private JPanel definePortStructure() {
		port = new JTextField();
		port.setMaximumSize(new Dimension(100,30));
		
		JLabel portlabel = new JLabel("Port: ");
		
		final JPanel panelServer = new JPanel();
		panelServer.setLayout(new BoxLayout(panelServer, BoxLayout.LINE_AXIS));
		
		panelServer.add(portlabel);
		panelServer.add(port);
		return panelServer;
	}

	private JPanel setUpFrame() {
		frame = new JFrame("Back up server");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				exitOrHide();
			}
		});
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.setMinimumSize(new Dimension(300,60));
		return panel;
	}
	
	private void exitOrHide() {
		if(s != null && s.isRunning()){
			frame.setVisible(false);
			new Tray(this);
		} else{
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		}
	}

	public static void main(String[] args){
		if(args.length == 0)
			new ServerGUI();
		else{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
				reader.readLine();
				String[] port = reader.readLine().split(": ");
				reader.close();
				if(port.length > 1){
					new ServerGUI(Integer.parseInt(port[1]));
				}
				else 
					return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public JFrame getFrame(){
		return frame;
	}

	public static void stopServer() {
		s.stopActivity();
	}

	public Server getServer() {
		return s;
	}

	public void setOpeningTray(int port) {
		this.port.setText(Integer.toString(port));
		this.port.setEnabled(false);
		run.setText("Stop");
	}
}
