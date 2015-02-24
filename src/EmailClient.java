import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class EmailClient 
{
	String hostname;
	public static final int PORT_NAME = 25;
	
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;

	public void connectToServer(String hostname) throws UnknownHostException, IOException
	{
		clientSocket = new Socket(hostname, PORT_NAME);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public void startEmailServerName(InputStream is)
	{
		System.out.print("Please input server name: ");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(is));
		String userInput = "";
		
		try {
			userInput = inFromUser.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Yo, you somehow messed it up");
			return;
		}
		
		hostname = userInput;
		try {
			connectToServer(userInput);
		} catch (IOException e) {
			System.out.println("Incorrect host! Exiting!!!");
			e.printStackTrace();
			startEmailServerName(is);
		}
	}
	
	public void startEmailHandshake()
	{
		
	}
	
}
