package voter;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
 
public class Mc {
 
	public static void main(String[] args) throws Exception
	{
		Socket s = new Socket("127.0.0.1", 8000);
 
		// new Thread to read content from server.
		new Thread(new ClientThread(s)).start();
 
		PrintStream ps = new PrintStream(s.getOutputStream());
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line = br.readLine()) != null)
		{
			ps.println(line);
		}
	}
}
class ClientThread implements Runnable
{
	private Socket s = null;
	BufferedReader br = null;
	public ClientThread(Socket s) throws IOException
	{
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	public void run()
	{
		try
		{
			String content = null;
			while( (content = br.readLine()) != null)
			{
				System.out.println(content);
			}
		}
		catch (IOException io)
		{
			io.printStackTrace();
		}
	}
}
