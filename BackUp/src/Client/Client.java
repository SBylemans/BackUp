package Client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	private void sendFileNameAndLength(File file){
		try {
			clientToServer.writeInt(file.getAbsolutePath().length());
			clientToServer.flush();
			clientToServer.writeBytes(file.getAbsolutePath());
			clientToServer.flush();
			clientToServer.writeLong(file.length());
			clientToServer.flush();
			clientToServer.writeBoolean(file.isDirectory());
			clientToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendFile(File file){
		try{
			FileInputStream fis = new FileInputStream(file);

			BufferedInputStream bis = new BufferedInputStream(fis);
			int total = 0;
			int count = 0;
			long length = file.length();
			while(total < length && (count = bis.read(buffer, 0, (int) Math.min(buffer.length, length-total))) > 0){
				clientToServer.write(buffer,0,(int) Math.min(buffer.length, length-total));
				total += count;
			}
			System.out.println(serverToClient.readUTF());
			bis.close();
			fis.close();
		}catch (IOException e){
			System.out.println("Could not send file");
		}
	}

	/**
	 * Sends all files and content of the files to the back-up server
	 * @param filePath
	 */
	public void backUp(String filePath){
		ArrayList<File> files = new ArrayList<File>();
		boolean dir = false;
		try{
			clientToServer.writeUTF("backup");
			listf(filePath, files);
			clientToServer.writeInt(files.size());
			for(File fi : files){
				sendFileNameAndLength(fi);
				dir = fi.isDirectory();
				if(!dir){
					sendFile(fi);
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

	private void restore(String fileToRestore, String location) {
		String name = "";
		long length;
		boolean dir;
		try {
			clientToServer.writeUTF("restore");
			sendFileNameAndLength(new File(fileToRestore));
			int size = this.serverToClient.readInt();
			for(int i = 0; i < size; i++){
				name = readFileName(serverToClient, buffer);
				String sub = name.substring(name.lastIndexOf("/"));
				String path = location + sub;
				File file = new File(path);
				length = serverToClient.readLong();
				dir = serverToClient.readBoolean();
				if(!dir){
					receiveFile(serverToClient,clientToServer,file, length);
				} else file.mkdirs();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TODO
	private void receiveFile(DataInputStream reader, DataOutputStream writer, File file, long length) {
		try{
			int t = file.getAbsolutePath().lastIndexOf("/");
			String dirs = file.getAbsolutePath().substring(0, t);
			System.out.println("File: " + file.getAbsolutePath());
			int p = dirs.lastIndexOf("/");
			String last = dirs.substring(p);
			String directory = "D:/backup" + last;
			System.out.println(directory);
			File direcs = new File(directory);
			
			direcs.mkdirs();
			File newFile = new File(directory+file.getAbsolutePath().substring(t));
			FileOutputStream fos = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int total = 0;
			int count = 0;
			while(total < length && (count = reader.read(buffer, 0, (int) Math.min(buffer.length, length-total))) > 0){
				bos.write(buffer,0,count);
				total += count;
			}
			writer.writeUTF("File " + file.getAbsolutePath() + " is created!");
			writer.flush();
			bos.close();
			fos.close();
		} catch(IOException e){

		}
	}

	private void get() {
		try {
			clientToServer.writeUTF("get");
			int files = serverToClient.readInt();
			System.out.println("# files: " + files );
			for(int i = 0; i < files; i++){
				String name = readFileName(serverToClient, buffer);
				Long fileSize = serverToClient.readLong();
				int index = 0;
				while(fileSize/100 > 1){
					fileSize /= 100;
					index++;
				}
				System.out.print(String.format("%-18s","Size of file: " + fileSize));
				System.out.print(String.format("%-5s",  FileSize.values()[index]));
				
				serverToClient.readBoolean();
				System.out.print(name);
				System.out.println();
			}
		} catch (IOException e) {
			
		}
	}
	
	private String readFileName(DataInputStream reader, byte[] b){
		String name = "";
		try{
			int fileNameLength = reader.readInt();
			int total = 0;
			int count = 0;
			//TODO
			while(total < fileNameLength && (count = reader.read(b, 0, (int) Math.min(b.length, fileNameLength-total))) > 0){
				name += new String(b,0,count);
				total += count;
			}
		} catch(IOException e){
			
		}
		return name;
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
			client.get();
		} else if(args[2].equalsIgnoreCase("restore")){
			client.restore(args[3],args[4]);
		}
		client.stop();
		
	}
}
