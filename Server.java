import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/* The Server class acts as the Server side for a file sharing system.
 * It opens a server on the machine and listens on the port assigned to the instance
 * field SOCKET_PORT. Only one client can connect at a time, multithreading required to allow for 
 * multiple client connections at once. Currently supports commands: "ls", "ls -a", "cd <file name>",
 * "cd ../", "get <file name>" , and "get <name of file to be received by client> <name to save
 * file under on client side>"
 */
@SuppressWarnings("resource") //Hides any "unused resource" warnings
public class Server {
	
	//Change to root directory of the server's machine.
	private static final File ROOT_DIRECTORY = new File("C:\\Users\\AlexK\\Desktop");
	public static final int SOCKET_PORT = 1234; //The port for the server to listen on
	
	private static File currentFile = new File("/Users/josh/Desktop/Temp/Server"); //The current file that the client is interacting with
	
	public static void main(String[] args) throws IOException {
		System.out.println("Starting Server...");
		ServerSocket socket = null;
		
		currentFile = ROOT_DIRECTORY;
		try {
			socket = new ServerSocket(1234); //Create server and start listener
		} catch (Exception e) {
			System.out.println("Could not start server... Terminating");
			e.printStackTrace();
		}
		System.out.println("Server successfully started.");
		
		try {
			while(true) {
				Socket client = socket.accept(); //Command has been received... Parse it
				try {
					Scanner clientScanner = new Scanner(client.getInputStream());		
					
					String input = "";
					try {
						input = clientScanner.nextLine(); //Read the input
					} catch(Exception e) {
						
					}
					
					String[] arguments = input.split(" "); //Split the command into its parts
					
					//Parse the command and execute it.
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
					client.close(); //Close the client's stream
				}
			} 
		} finally {
			socket.close(); //Close the server's stream
		}		
	}
	
	/* Prints the contents of a given directory not including hidden files (Files that begin with a ".")
	 * @param file The file to print the contents of
	 */
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
	
	/* Prints the contents of a given directory including hidden files (Files that begin with a ".")
	 * @param file The file to print the contents of
	 */
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
	
	/* Sends a file to a given socket
	 * @param FILE_TO_SEND The path to the file to send
	 * @param sock The socket to send the file to
	 */
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
