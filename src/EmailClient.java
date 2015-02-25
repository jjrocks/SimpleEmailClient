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
		System.out.println(inFromServer.readLine());
	}
	
	public void startEmailServerName(InputStream is)
	{
		System.out.print("Please input server name: ");
		String userInput = getUserInput(is);
		
		hostname = userInput;
		try {
			connectToServer(userInput);
			if (startEmailHandshake())
			{
				interactionSeries(is);
			}
			else
			{
				System.out.println("Incorrect host. Exiting");
				return;
			}
		} catch (IOException e) {
			System.out.println("Incorrect host! Exiting!!!");
			e.printStackTrace();
			startEmailServerName(is);
		}
	}
	
	/**
	 * The email handshake. 	
	 */
	public boolean startEmailHandshake()
	{
		try {
			outToServer.writeBytes("HELO cs.lafayette.edu" + " \r\n");
			return checkStatus(inFromServer.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void interactionSeries(InputStream is)
	{
		String from = "";
		System.out.print("Who do you want to send it as: ");
		from = getUserInput(is);
		if(!checkStatus(sendToServer("MAIL FROM: <" + from + ">\r\n")))
		{
			System.out.println("Failed!");
			return;
		}
		System.out.print("Which people do you want to send it to (seperate by commas): ");
		String[] toArray = getUserInput(is).split(",");
		for(String person : toArray)
		{
			sendToServer("RCPT TO: <" + person + "> \r\n");
		}
		sendToServer("DATA \r\n");
		System.out.print("What message do you want to send to people: ");
		
		String data = getUserInput(is);
		if(!checkStatus(sendToServer(data + "\r\n.\r\n")))
		{
			System.out.println("Didn't send properly! Sorry!");
			return;
		}
		//sendToServer("\r\n.\r\n");
		sendToServer("QUIT\r\n");
		System.out.println("Yay it worked!");
	}
	
	public boolean checkStatus(String line)
	{
		return line.contains("220") || line.contains("250") || 
				line.contains("354") || line.contains("221");
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
			String line = inFromServer.readLine();
			sb.append(line);
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
