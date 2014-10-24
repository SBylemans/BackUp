package Server;



import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Tray {

    private static final String TOOLTIP = "Server";
    private static final String IMAGE = "/home/sander/git/BackUp/BackUp/src/Image/server.png";
    private static PopupMenu menu;
	private ServerGUI gui;

    public Tray(ServerGUI gui){
    	this.gui = gui;
    	start();
    }

    public Tray() {
    	start();
	}

	private void start() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image trayImage = Toolkit.getDefaultToolkit().getImage(IMAGE);
            
            int trayIconWidth = new TrayIcon(trayImage).getSize().width;
            TrayIcon trayIcon = new TrayIcon(trayImage.getScaledInstance(trayIconWidth, 24, Image.SCALE_SMOOTH));
            createMenu(tray, trayIcon);
            try {
                tray.add(trayIcon);
                trayIcon.setPopupMenu(menu);
                trayIcon.setToolTip(TOOLTIP);
            } catch (AWTException e) {
                System.err.println("Error starting tray: " + e);
            }

        } else {
            System.err.println("SystemTray not supported");
        }
    }

    private void createMenu(final SystemTray tray,final TrayIcon trayIcon) {
        menu = new PopupMenu();
        MenuItem open = new MenuItem("Open");
        MenuItem exit = new MenuItem("Quit");
        exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerGUI.stopServer();
				System.exit(0);
			}
		});
        open.addActionListener(new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) {
        		gui.getFrame().setVisible(true);
        		gui.setOpeningTray(gui.getServer().getPort());
        		tray.remove(trayIcon);
        	}
		});
        menu.add(open);
        menu.addSeparator();
        menu.add(exit);
    }
}