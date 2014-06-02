package Server;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
	Tray t;
	
	public ServerGUI(){
		final JFrame frame = new JFrame("Back up server");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				exitOrHide(frame);
			}
		});
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.setMinimumSize(new Dimension(300,60));
		
		final JTextField port = new JTextField();
		port.setMaximumSize(new Dimension(100,30));
		
		JLabel portlabel = new JLabel("Port: ");
		
		final JPanel panelServer = new JPanel();
		panelServer.setLayout(new BoxLayout(panelServer, BoxLayout.LINE_AXIS));
		
		panelServer.add(portlabel);
		panelServer.add(port);
		

		final JButton run = new JButton("Run");
		panelServer.add(run);
		
		
		
		final JPanel runAlways = new JPanel();
		runAlways.setLayout(new BoxLayout(runAlways, BoxLayout.LINE_AXIS));
		
		final JCheckBox check = new JCheckBox("Run on boot");
		runAlways.add(check);
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(run.getText().equals("Run")){
					String p = port.getText();
					s = new Server(Integer.parseInt(p));
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
					}
					run.setText("Stop");
				}
				else{
					s.stopActivity();
					run.setText("Run");
				}

			}
		});
		
		panel.add(panelServer);
		panel.add(runAlways);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Image/server.png")));
		
		frame.add(panel);
		frame.setVisible(true);
	}
	
	private void exitOrHide(JFrame frame) {
		if(s != null && s.isRunning()){
			frame.setVisible(false);
			new Tray(frame);
		} else{
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		}
	}

	public static void main(String[] args){
		if(args.length == 0)
			new ServerGUI();
		else
			s = new Server(Integer.parseInt(args[0]));
	}

	public static void stopServer() {
		s.stopActivity();
	}
}
