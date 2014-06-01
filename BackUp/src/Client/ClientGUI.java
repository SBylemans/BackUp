package Client;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ClientGUI {

	public ClientGUI(){
		JFrame frame = new JFrame("Back up client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(400,400));
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		final JTextField name = new JTextField();
		name.setMaximumSize(new Dimension(100,30));
		final JTextField port = new JTextField();
		port.setMaximumSize(new Dimension(100,30));
		
		JLabel namelabel = new JLabel("Name: ");
		JLabel portlabel = new JLabel("Port: ");
		
		final JPanel panelServer = new JPanel();
		panelServer.setLayout(new BoxLayout(panelServer, BoxLayout.LINE_AXIS));
		
		panelServer.add(namelabel);
		panelServer.add(name);
		panelServer.add(portlabel);
		panelServer.add(port);
		
		panel.add(panelServer);
		
		final JTextArea files = new JTextArea();
		files.setEditable(false);
		JScrollPane scroll = new JScrollPane(files);
		panel.add(scroll);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		JButton selectFile = new JButton("Select Files");
		selectFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(panel);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            if(!files.getText().contains(file.getAbsolutePath())){
		            	files.append(file.getAbsolutePath() + "\n");
		            }
		            else{
		            	JOptionPane.showMessageDialog(panel, "File is already in list");
		            }
		        }
			}
		});
		JButton removeFile = new JButton("Remove");
		removeFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(panel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if(files.getText().contains(file.getAbsolutePath()))
						files.setText(files.getText().replace(file.getAbsolutePath() + "\n", ""));
					else
						JOptionPane.showMessageDialog(panel, "No such file in list");
				}
			}
		});
		
		//New frame met statusbar
		
		
		JButton backUp = new JButton("Back Up");
		backUp.setAlignmentX(Component.CENTER_ALIGNMENT);
		backUp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = files.getText();
				if(name.getText().equals("") || port.getText().equals("")){
					JOptionPane.showMessageDialog(panel, "No name or port of server given");
				}
				 
				else if(!string.equals("")){
					
					String[] split = string.split("\n");
					JProgressBar progressBar = new JProgressBar(0, split.length);
				    progressBar.setValue(0);
				    progressBar.setStringPainted(true);
					for(String f : split){
						Client client = new Client(name.getText(), Integer.parseInt(port.getText()));
						client.backUp(f);
					}
					files.setText("");
					
					JOptionPane.showMessageDialog(panel, "All files backed up");
				} else{
					JOptionPane.showMessageDialog(panel, "No files to back up");
				}
			}
		});
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
		
		panelButtons.add(selectFile);
		panelButtons.add(Box.createRigidArea(new Dimension(10, 0)));
		panelButtons.add(removeFile);
		panelButtons.add(Box.createRigidArea(new Dimension(10, 0)));
		panelButtons.add(backUp);
		
		
		panel.add(panelButtons);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Image/client.png")));
		
		frame.add(panel);
		frame.setVisible(true);
		
		
	}
	
	public static void main(String[] args){
		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (UnsupportedLookAndFeelException e) {
			System.out.println("Not supported");
		}
		catch (ClassNotFoundException e) {
			System.out.println("Not found");
		}
		catch (InstantiationException e) {
			System.out.println("Not initiated");
		}
		catch (IllegalAccessException e) {
			System.out.println("Illegal Acces");
		}
		new ClientGUI();
	}
	
}
