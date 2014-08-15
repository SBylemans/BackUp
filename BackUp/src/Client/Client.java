package Client;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class Client {
	private DataInputStream serverToClient;
	private Socket client;
	private DataOutputStream clientToServer;
	private byte[] buffer;

	public Client(String name, int port){
	    try {
	    	client = new Socket(name, port);
	        //receive response server
	        serverToClient = new DataInputStream(client.getInputStream());
	        //send message to server
	        clientToServer = new DataOutputStream(client.getOutputStream());

			 buffer = new byte[2048];
	    }
	    catch (IOException e) {
	    }
	}
	
	/**
	 * Closes all connections
	 */
	public void stop(){
		
		try {
			client.close();
			serverToClient.close();
			clientToServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends all files and content of the files to the back-up server
	 * @param filePath
	 */
	public void backUp(String filePath){
		ArrayList<File> files = new ArrayList<File>();
		boolean dir = false;
		long length = 0;
		try{
			clientToServer.writeUTF("backup");
			listf(filePath, files);
			clientToServer.writeInt(files.size());
			int total;
			int count;
			for(File fi : files){
				dir = false;
				if(fi.isDirectory()) dir = true;
				clientToServer.writeInt(fi.getAbsolutePath().length());
				System.out.println(fi.getAbsolutePath().length());
				clientToServer.writeBytes(fi.getAbsolutePath());
				length = fi.length();
				clientToServer.writeLong(length);
				clientToServer.writeBoolean(dir);
				if(!dir){
					FileInputStream fis = new FileInputStream(fi);
					BufferedInputStream bis = new BufferedInputStream(fis);
					total = 0;
					count = 0;
					while(total < length && (count = bis.read(buffer, 0, (int) Math.min(buffer.length, length-total))) > 0){
						clientToServer.write(buffer,0,(int) Math.min(buffer.length, length-total));
						total += count;
					}
					System.out.println(serverToClient.readUTF());
					bis.close();
					fis.close();
				}
			}

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get all files, folders and subfolders in the file specified by directoryName, recursively.
	 * @param directoryName
	 * @param files
	 * @param size
	 */
	private void listf(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);
	    if(directory.isFile()){
	    	files.add(directory);
	    }
	    else{
	    	File[] fList = directory.listFiles();
	    	if(directory.isDirectory() && directory.listFiles().length == 0) files.add(directory);
	    	else{
	    		for (File file : fList) {
	    			if(file.isDirectory() && file.listFiles().length == 0) files.add(file);
	    			else{
	    				if (file.isFile()) {
	    					files.add(file);
	    				} else if (file.isDirectory()) {
	    					listf(file.getAbsolutePath(), files);
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
	public static void main(String[] args){
		String name = args[0];
		int port = Integer.parseInt(args[1]);
		System.out.println("Name: " + name + " Port: " + port);
		Client client = new Client(name, port);
		if(args[2].equalsIgnoreCase("backup")){
			System.out.println(args[3]);
			File file = new File(args[3]);
			if(file.exists())client.backUp(args[3]);
			else System.out.println("File doesn't exist");
		} else if(args[2].equalsIgnoreCase("get")){
			String[] backedUpFiles = client.get(args[3]);
		}
		client.stop();
		
	}

	private String[] get(String string) {
		return null;
		// TODO Auto-generated method stub
		
	}
}
