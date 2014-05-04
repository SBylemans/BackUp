import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;


public class ClientGUI {

	public ClientGUI(){
		JFrame frame = new JFrame("Back up to server");
		frame.setMinimumSize(new Dimension(400,400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		final JTextField name = new JTextField();
		final JTextField port = new JTextField();
		
		JLabel namelabel = new JLabel("Name: ");
		JLabel portlabel = new JLabel("Port: ");
		
		final JTextArea files = new JTextArea();
		files.setEditable(false);
		JScrollPane scroll = new JScrollPane(files);
		panel.add(scroll);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		JButton selectFile = new JButton("Select Files");
		selectFile.setAlignmentX(panel.CENTER_ALIGNMENT);
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
		removeFile.setAlignmentX(panel.CENTER_ALIGNMENT);
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
		
		JButton backUp = new JButton("Back Up");
		backUp.setAlignmentX(panel.CENTER_ALIGNMENT);
		backUp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = files.getText();
				if(!string.equals("")){
					String[] split = string.split("\n");
					for(String f : split){
						Client client = new Client("localhost", 2121);
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
		panelButtons.add(removeFile);
		panelButtons.add(backUp);
		
		panel.add(panelButtons);
		
		frame.add(panel);
		frame.setVisible(true);
		
		
	}
	
	public static void main(String[] args){
		new ClientGUI();
	}
	
}
