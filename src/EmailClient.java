import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

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
		String userInput = getUserInput(is);
		
		hostname = userInput;
		try {
			connectToServer(userInput);
			startEmailHandshake();
		} catch (IOException e) {
			System.out.println("Incorrect host! Exiting!!!");
			e.printStackTrace();
			startEmailServerName(is);
		}
	}
	
	public void startEmailHandshake()
	{
		try {
			outToServer.writeBytes("HELO " + hostname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void interactionSeries(InputStream is)
	{
		String from = "";
		System.out.print("Who do you want to send it as: ");
		from = getUserInput(is);
		sendToServer("MAIL FROM: <" + from + ">");
		System.out.print("Which people do you want to send it to (seperate by commas): ");
		String[] toArray = getUserInput(is).split(",");
		sendToServer("RCPT TO: <" + toArray[0] + ">");
		sendToServer("DATA");
		System.out.print("What message do you want to send to people: ");
		
		String data = getUserInput(is);
		sendToServer(data);
		sendToServer(".");
		sendToServer("QUIT");
	}
	
	public String getUserInput(InputStream is)
	{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(is));
		String userInput = "";
		
		try {
			userInput = inFromUser.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Yo, you somehow messed it up");
			return "";
		}
		return userInput;
	}
	
	public String sendToServer(String outMessage)
	{
		StringBuilder sb = new StringBuilder();
		try {
			outToServer.writeBytes(outMessage);
			String line;
			while((line = inFromServer.readLine()) != null)
			{
				sb.append(line);
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		EmailClient client = new EmailClient();
		client.startEmailServerName(System.in);
	}
	
}
