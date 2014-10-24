package Server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread {

	private static boolean notStopped = false;
	private ServerSocket server;
	private Socket acceptingSocket;

	private byte[] b = new byte[2048];
	
	public Server(int port){
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Try again");
		}
	}
	
	/**
	 * Reads all files and puts them in the back-up folder. Also creates the appropriate dirs.
	 */
	public void run(){
		notStopped = true;
		BufferedInputStream buffer = null;
		DataInputStream reader = null;
		BufferedOutputStream out = null;
		DataOutputStream writer = null;
		int size = 0;
		String command = "";
		try {
			acceptingSocket = server.accept();
			buffer = new BufferedInputStream(acceptingSocket.getInputStream());
			reader = new DataInputStream(buffer);
			out = new BufferedOutputStream(acceptingSocket.getOutputStream());
			writer = new DataOutputStream(out);
			command = reader.readUTF();
			if(command.equalsIgnoreCase("backup")){
				size = reader.readInt();
				System.out.println("Size: " + size);
				backup(writer, reader, size);
			}
			else if(command.equalsIgnoreCase("get")){
				System.out.println("Getting");
				sendFileNames(writer);
			} else if(command.equalsIgnoreCase("restore")){
				System.out.println("Restoring");
				sendFiles(writer, reader);
			}
		} catch (IOException e1) {
		}
	}
	
	public void sendFiles(DataOutputStream writer, DataInputStream reader){
		ArrayList<File> files = new ArrayList<File>();
		boolean dir = false;
		try{
			listf(filePath, files);
			writer.writeInt(files.size());
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
	
	private void sendFileNames(DataOutputStream writer) {
		ArrayList<File> files = new ArrayList<File>();
		listf("/backup", files);
		try {
			writer.writeInt(files.size());
			writer.flush();
		} catch (IOException e) {
			
		}
		for(File f : files){
			sendFileNameAndLength(f, writer);
		}
	}
	
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

	private String readFileName(DataInputStream reader){
		String name = "";
		try{
			int fileNameLength = reader.readInt();
			System.out.println("Filenamelength: " + fileNameLength);
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
	
	private void sendFileNameAndLength(File file, DataOutputStream writer){
		try {
			writer.writeInt(file.getAbsolutePath().length());
			writer.flush();
			writer.writeBytes(file.getAbsolutePath());
			writer.flush();
			writer.writeLong(file.length());
			writer.flush();
			writer.writeBoolean(file.isDirectory());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void backup(DataOutputStream writer, DataInputStream reader, int size) {
		long length=0;
		boolean dir  = false;
		File file;
		String name = "";
		for(int j = 0; j < size; j++){
			try {
				name = readFileName(reader);
				String path = new String(name);
				length = reader.readLong();
				System.out.println("File length: " + length);
				dir = reader.readBoolean();
				path = "/backup" + path;
				file = new File(path);
				if(!dir){
					receiveFile(reader,writer,file, length);
				} else file.mkdirs();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receiveFile(DataInputStream reader, DataOutputStream writer, File file, long length) {
		try{
			int t = file.getAbsolutePath().lastIndexOf("/");
			String dirs = file.getAbsolutePath().substring(0, t);
			System.out.println("File: " + file.getAbsolutePath());
			int p = dirs.lastIndexOf("/");
			String last = dirs.substring(p);
			String directory = "/backup" + last;
			System.out.println(directory);
			File direcs = new File(directory);
			
			direcs.mkdirs();
			File newFile = new File(directory+file.getAbsolutePath().substring(t));
			FileOutputStream fos = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int total = 0;
			int count = 0;
			while(total < length && (count = reader.read(b, 0, (int) Math.min(b.length, length-total))) > 0){
				bos.write(b,0,count);
				total += count;
			}
			writer.writeUTF("File " + file.getAbsolutePath() + " is created!");
			writer.flush();
			bos.close();
			fos.close();
		} catch(IOException e){

		}
	}

	public void stopActivity(){
		try {
			acceptingSocket.close();
		} catch (IOException | NullPointerException e) {
			if(e.getClass().equals(IOException.class))
				stopActivity();
		}
		try {
			server.close();
			System.out.println("Done");
			notStopped = false;
		} catch (IOException e) {
			stopActivity();
		}
	}
	
	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
		Server server = new Server(port);
		do
			server.run();
		while(notStopped);
	}

	public boolean isRunning() {
		return notStopped;
	}

	public int getPort() {
		return server.getLocalPort();
	}
}
