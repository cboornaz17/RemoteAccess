import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SampleClient {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		Socket socket = new Socket("129.161.222.179", 1234); //Create a socket to connect to the server
		Scanner serverScanner = new Scanner(socket.getInputStream());
		
		System.out.println("Enter a number");
		int cmd = scanner.nextInt(); //Read in the command
		
		PrintStream p = new PrintStream(socket.getOutputStream()); //Send the command to the server
		p.println(cmd);
		
		int result = serverScanner.nextInt();
		System.out.println(result);
		
		p.close();
		serverScanner.close();
		socket.close();
		scanner.close();
	}
}
