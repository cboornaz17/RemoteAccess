import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SampleServer {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		System.out.println("Starting Server...");
		ServerSocket socket = new ServerSocket(1234); //Create server and start listener
		System.out.println("Server successfully started.");
		
		Socket client = socket.accept(); //Command has been received... Parse it
		Scanner clientScanner = new Scanner(client.getInputStream());
		
		int input = clientScanner.nextInt();
		int result = 2*input;
		
		PrintStream p = new PrintStream(client.getOutputStream());
		p.println(result);
	}

}
