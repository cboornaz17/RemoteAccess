import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	private static final File ROOT = new File("/Users/josh/Desktop/Temp/Client/");
	
	public final static int SOCKET_PORT = 1234;
	public final static String SERVER = "128.113.153.85";
	public final static int FILE_SIZE = 50000000;

	public static void main(String[] args) throws IOException {		
		while(true) {
			
			Scanner scanner = new Scanner(System.in);
			Socket socket = new Socket(SERVER, SOCKET_PORT);
			Scanner serverScanner = new Scanner(socket.getInputStream());
			
			System.out.println("Enter a command");
			String cmd = scanner.nextLine();
			
			if(cmd.equalsIgnoreCase("exit")) {
				System.out.println("Exiting...");
				break;
			}
			
			PrintStream p = new PrintStream(socket.getOutputStream());
			p.println(cmd);
			
			String[] arguments = cmd.split(" ");
			
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
			
			String result = "";
			while(serverScanner.hasNextLine()) {
				result += serverScanner.nextLine() + "\n";
			}
			
			System.out.println(result);
		}
	}

	public static void getFile(String FILE_TO_RECEIVED, Socket sock) throws UnknownHostException, IOException {
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			byte[] mybytearray = new byte[FILE_SIZE];
			InputStream is = sock.getInputStream();
			fos = new FileOutputStream(FILE_TO_RECEIVED);
			bos = new BufferedOutputStream(fos);

			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead < 0);

			bos.write(mybytearray, 0, current);
			bos.flush();
			System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + current + " bytes read)");
		} finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
		}
	}
}
