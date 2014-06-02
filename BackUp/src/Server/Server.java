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
		int fileNameLength = 0;
		int size = 0;
		try {
			acceptingSocket = server.accept();
			buffer = new BufferedInputStream(acceptingSocket.getInputStream());
			reader = new DataInputStream(buffer);
			out = new BufferedOutputStream(acceptingSocket.getOutputStream());
			writer = new DataOutputStream(out);
			size = reader.readInt();
			System.out.println("Size: " + size);
		} catch (IOException e1) {
		}
		long length=0;
		boolean dir  = false;
		int t = 0;
		String dirs = "";
		File direcs;
		File file;
		int total;
		int count;
		String name = "";
		for(int j = 0; j < size; j++){
			try {
				name = "";
				fileNameLength = reader.readInt();
				System.out.println("Filenamelength: " + fileNameLength);
				total = 0;
				count = 0;
				//TODO
				while(total < fileNameLength && (count = reader.read(b, 0, (int) Math.min(b.length, fileNameLength-total))) > 0){
					name += new String(b,0,count);
					total += count;
				}
				String path = new String(name);
				System.out.println("Path: " + path);
				length = reader.readLong();
				dir = reader.readBoolean();
				System.out.println(dir);
				path = "/backup" + path;
				file = new File(path);
				if(!dir){
					t = file.getAbsolutePath().lastIndexOf("/");
					dirs = file.getAbsolutePath().substring(0, t);
					direcs = new File(dirs);
					direcs.mkdirs();
					FileOutputStream fos = new FileOutputStream(file);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    total = 0;
					count = 0;
					while(total < length && (count = reader.read(b, 0, (int) Math.min(b.length, length-total))) > 0){
						bos.write(b,0,count);
						total += count;
					}
					writer.writeUTF("File " + file.getAbsolutePath() + " is created!");
					writer.flush();
					bos.close();
					fos.close();

				} else file.mkdirs();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
}
