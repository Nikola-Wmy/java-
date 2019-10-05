package voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import javax.swing.JOptionPane;
 
public class Ms {
 
	public static ArrayList<Socket> socketlist = new ArrayList<Socket>();
	private static String port ;
	public static void main(String[] args) throws IOException
	{
		//弹出一个对话框输入端口号
		
		
		
		ServerSocket ss = new ServerSocket(8000);
		System.out.println("Server is initializating...");
		while(true)
		{
			System.out.println("Server is waiting...");
			
			//此处将阻塞监听
			Socket s = ss.accept();
			System.out.println("listening from: " + s.getInetAddress());
			socketlist.add(s);
			new Thread(new ServerThread(s)).start();
		}
	}
 
}

class ServerThread implements Runnable
{
	Socket s = null;
	BufferedReader br = null;
	public ServerThread(Socket s) throws IOException
	{
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	public void run()
	{
		try
		{
			String content = null;
			while( (content = readFromClient()) != null )
			{
				//播报每个客户端数据
				for(Socket s : Ms.socketlist)
				{
					PrintStream ps = new PrintStream(s.getOutputStream());
					ps.println(content);
				}
			}
		}
		catch (IOException io) 
		{
			io.printStackTrace();
		}
	}
	private String readFromClient()
	{
		try
		{
			return br.readLine();
		}
		catch (IOException io)
		{
			Ms.socketlist.remove(s);
			System.out.println(s.getInetAddress() + " is disconnecting...");
		}
		return null;
	}
	
}