import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class ClientGUI {

	public ClientGUI(){
		JFrame frame = new JFrame("Back up to server");
		frame.setMinimumSize(new Dimension(400,400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		final JTextArea files = new JTextArea();
		files.setEditable(false);
		JScrollPane scroll = new JScrollPane(files);
		panel.add(scroll);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		JButton selectFile = new JButton("Select Files");
		selectFile.setAlignmentX(panel.CENTER_ALIGNMENT);
		selectFile.setAlignmentY(panel.BOTTOM_ALIGNMENT);
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
		removeFile.setAlignmentY(panel.BOTTOM_ALIGNMENT);
		removeFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(panel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					//files.getText().replace(file.getAbsolutePath(), "");
					files.setText(files.getText().replace(file.getAbsolutePath() + "\n", ""));
				}
			}
		});
		
		JButton backUp = new JButton("Back Up");
		backUp.setAlignmentX(panel.CENTER_ALIGNMENT);
		backUp.setAlignmentY(panel.BOTTOM_ALIGNMENT);
		panel.add(selectFile);
		panel.add(removeFile);
		panel.add(backUp);
		
		frame.add(panel);
		frame.setVisible(true);
		
		
	}
	
	public static void main(String[] args){
		new ClientGUI();
	}
	
}
