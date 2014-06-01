package Server;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ServerGUI {

	Server s = null;
	
	public ServerGUI(){
		JFrame frame = new JFrame("Back up server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(run.getText().equals("Run")){
					String p = port.getText();
					s = new Server(Integer.parseInt(p));
					s.start();
					run.setText("Stop");
				}
				else{
					s.stopActivity();
					run.setText("Run");
				}

			}
		});
		
		final JPanel runAlways = new JPanel();
		runAlways.setLayout(new BoxLayout(runAlways, BoxLayout.LINE_AXIS));
		
		final JCheckBox check = new JCheckBox("Run on boot");
		runAlways.add(check);
		
		panel.add(panelServer);
		panel.add(runAlways);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Image/server.png")));
		
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		new ServerGUI();
	}
}
