import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/* The Client class creates a client that communicates with the Server class
 * on the server assigned to the SERVER instance field below and the port assigned
 * to the SOCKET_PORT instance field below. Mimics basic UNIX commands. 
 */
@SuppressWarnings("resource") //Hides any "unused resource" warnings
public class Client {
	
	//Change to the directory that you want to interface with the server. All files will be saved there or in a subdirectory.
	private static File ROOT = new File(System.getProperty("user.home"));//new File("/Users/josh/Desktop/Temp/Client/");
	
	public final static int SOCKET_PORT = 1234; //The port to connect to
	public final static String SERVER = "128.113.153.85"; //The address to connect to
	public final static int FILE_SIZE = 2_000_000_000; //max file size of 2GB

	public static void main(String[] args) throws IOException {		
		
		if(args.length == 1) {
			ROOT = new File(args[0]);
		}
		System.out.println("Looking for server...");
		boolean hasConnected = false;
		while(true) {
			
			Scanner scanner = new Scanner(System.in);
			Socket socket = new Socket(SERVER, SOCKET_PORT); //Create a socket to connect to the server
			Scanner serverScanner = new Scanner(socket.getInputStream());
			
			if(!hasConnected)
				System.out.println("Connection to server established!");
			
			System.out.println("Enter a command");
			String cmd = scanner.nextLine(); //Read in the command
			
			if(cmd.equalsIgnoreCase("exit")) {
				System.out.println("Exiting...");
				break;
			}
			
			PrintStream p = new PrintStream(socket.getOutputStream()); //Send the command to the server
			p.println(cmd);
			
			String[] arguments = cmd.split(" ");
			
			//TODO: Move this logic to Server side
			//Do client side work here
			if(arguments[0].equalsIgnoreCase("get")) {
				if(arguments.length == 2) {
					getFile(ROOT.getAbsolutePath() + "/" + arguments[1], socket);
					System.out.print("\n\n");
				} else if(arguments.length == 3) {
					getFile(ROOT.getAbsolutePath() + "/" + arguments[2], socket);
					System.out.println("\n\n");
				}
				continue;
			}
			
			//Receive the results from the Server and print it to the terminal
			String result = "";
			while(serverScanner.hasNextLine()) {
				result += serverScanner.nextLine() + "\n";
			}
			
			System.out.println(result);
		}
	}
	
	/* Receives an incoming file to a specified location
	 * @param FILE_TO_RECEIEVE The location (including new file name) to save the incoming file.
	 * @param sock The socket that is connected to the file's sender
	 */
	public static void getFile(String FILE_TO_RECEIVE, Socket sock) throws UnknownHostException, IOException {
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			byte[] mybytearray = new byte[FILE_SIZE];
			InputStream is = sock.getInputStream();
			fos = new FileOutputStream(FILE_TO_RECEIVE);
			bos = new BufferedOutputStream(fos);

			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead < 0);

			bos.write(mybytearray, 0, current);
			bos.flush();
			System.out.println("File " + FILE_TO_RECEIVE + " downloaded (" + current + " bytes read)");
		} finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
		}
	}
}
