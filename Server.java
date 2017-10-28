import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	
	private static final File ROOT_DIRECTORY = new File("/Users/josh/Desktop");
	public static final int SOCKET_PORT = 1234;
	
	private static File currentFile = new File("/Users/josh/Desktop");
	
	public static void main(String[] args) throws IOException {
		System.out.println("Starting Server...");
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(1234);
		} catch (Exception e) {
			System.out.println("Could not start server... Terminating");
			e.printStackTrace();
		}
		System.out.println("Server successfully started.");
		
		try {
			while(true) {
				Socket client = socket.accept();
				try {
					Scanner clientScanner = new Scanner(client.getInputStream());		
					
					String input = "";
					try {
						input = clientScanner.nextLine();
					} catch(Exception e) {
						
					}
					
					String[] arguments = input.split(" ");
					
					if(arguments.length == 1) {
						if(arguments[0].equalsIgnoreCase("exit")) {
							continue;
						} else if(arguments[0].equalsIgnoreCase("clear")) {
							PrintStream p = new PrintStream(client.getOutputStream());
							p.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
							continue;
						}
					}
					
					if(arguments.length >= 1) {
						if(arguments[0].equalsIgnoreCase("ls")) {
							PrintStream p = new PrintStream(client.getOutputStream());
							String dir = "";
							dir = printDirectory(currentFile);
							if(arguments.length >= 2) {
								if(arguments[1].equalsIgnoreCase("-a")) {
									dir = printDirectoryAll(currentFile);
								}
							}
							
							p.println(dir);
						}
						
						if(arguments.length >= 2) {
							if(arguments[0].equalsIgnoreCase("cd")) {
								String fileName = arguments[1];
								PrintStream p = new PrintStream(client.getOutputStream());
								if(fileName.equalsIgnoreCase("../")) {
									File newFile = currentFile.getParentFile();
									if(!newFile.exists()) {
										p.println(printDirectory(currentFile));
									} else {
										currentFile = newFile;
										System.out.println(newFile.getAbsolutePath());
										p.println(printDirectory(currentFile));
									}
								} else {
									String newFile = currentFile.getAbsolutePath() + "/" + fileName;
									File f = new File(newFile);
									if(f.exists()) {
										currentFile = new File(f.getAbsolutePath());
										p.println(printDirectory(currentFile));
									}
								}
							} else if(arguments[0].equalsIgnoreCase("get")) {
								if(arguments.length >= 2) {
									String fileName = arguments[1];									
									sendFile(currentFile.getPath() + "/" + fileName, client);
								}
							}
						}
					}
				} finally {
						client.close();
				}
			} 
		} finally {
			socket.close();
		}		
	}
	
	private static String printDirectory(File file) {
		String s = "";
		
		File[] files = file.listFiles();
		
		if(files == null)
			return s;
		
		int count = 0;
		for(int i = 0; i < files.length; i++) {		
			if(count%4 == 0 && count != 0)
				s += "\n";
			
			if(files[i].isDirectory()) {
				if(!files[i].getName().startsWith(".")) {
					s += files[i].getName() + "/\t\t";
					count++;
				}
			} else {
				if(!files[i].getName().startsWith(".")) {
					s += files[i].getName() + "\t\t";
					count++;
				}
			}
		}
		s += "\n\n";
		return s;
	}
	
	private static String printDirectoryAll(File file) {
		String s = "";
		
		File[] files = file.listFiles();
		
		for(int i = 0; i < files.length; i++) {
			
			if(i%4 == 0)
				s += "\n";
			
			if(files[i].isDirectory())
				s += files[i].getName() + "/\t\t";
			else {
				s += files[i].getName() + "\t\t";
			}
		}
		s += "\n\n";
		return s;
	}
	
	public static void sendFile(String FILE_TO_SEND, Socket sock) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;

		try{
			File myFile = new File(FILE_TO_SEND);
			byte[] mybytearray = new byte[(int) myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
			os = sock.getOutputStream();
			System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
			System.out.println("Done.");
		} finally {
			if (bis != null)
				bis.close();
			if (os != null)
				os.close();
			if (sock != null)
				sock.close();
		}
	}
}
