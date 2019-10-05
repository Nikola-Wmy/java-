package voter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.TabableView;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class AcceptThread extends Thread{
	//存储向服务器发起连接的socke，用于发送投票结果
	ArrayList<Socket> socketlist = new ArrayList<Socket>();
	Socket clientSocket = null;
	ServerSocket listenSocket = Sever_2.listenSocket;
	String vote [] = Sever_2.vote;
	ArrayList<String> temp  = new ArrayList<String>();
	
	public void run() {
		//启动accept监听进程
		while(true) {			
			try {
				clientSocket = listenSocket.accept();
				socketlist.add(clientSocket);
				new Thread(new ServerThread_2(clientSocket)).start();			
				Sever_2.ta_info.appendText("客户机连接成功！客户机地址和端口："+clientSocket.getRemoteSocketAddress()+"\n");
		    BufferedWriter out = null;
		    out = new BufferedWriter(
	                 new OutputStreamWriter(            
	                 clientSocket.getOutputStream(),"utf-8"));
		    
		    //向客户端发送候选人
			for(int i=0;i<=vote.length;i++) {
				if(i == vote.length) {
					out.write("end"); 
		            out.newLine();
		            out.flush();
		            break;
				}
				out.write(vote[i]); 
	            out.newLine();
	            out.flush(); 
			}	
		   
			//停止监听标志
			if(this.interrupted()) {			
					break;
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}			
		}
		
		//调用类Map_Sort的倒序排序法，将候选人按票数倒序排列
		ServerThread_2.map = Map_Sort.sortDescend(ServerThread_2.map);
		
		//向客户端发送投票结果
		for(Socket s : socketlist) {		
			try {
				String map = null;
				BufferedWriter b;
				b = new BufferedWriter(
						new OutputStreamWriter(            
								s.getOutputStream(),"utf-8"));
				for(String key:ServerThread_2.map.keySet()) {
					Integer value = ServerThread_2.map.get(key);
					if(map==null) {
						map = key +":"+value+";";
					}else {
						map += key +":"+value+";";
					}
				}
				b.write(map); 
				b.newLine();
				b.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						 
		}
		
		//在result板输出投票结果
		Sever_2.ta_result.setText("");
		for(String key:ServerThread_2.map.keySet()) {
			Integer value = ServerThread_2.map.get(key);
			Sever_2.ta_result.appendText(key+":"+value+" 票!\n");
		} 				
	}		
}

class ServerThread_2 extends Thread
{
	public static Map<String,Integer> map = new HashMap<String,Integer>();
	ArrayList<String> contentlist = new ArrayList<String>();
	String vote [] = Sever_2.vote;
	String select  = null;
	Socket s = null;
	BufferedReader in = null;
    BufferedWriter out = null;
    String content = null;
        
	public  ServerThread_2(Socket s) throws IOException
	{
		this.s = s;
		
		//创建I/O流
		in = new BufferedReader(
                new InputStreamReader(
                s.getInputStream(),"utf-8"));           
        out = new BufferedWriter(
                 new OutputStreamWriter(            
                 s.getOutputStream(),"utf-8"));      
	}
	
	//synchronized 保证该代码块在同一时刻只能由一个线程运行
	synchronized public void run()
	{
		//检查map中是否有该候选人，没有则添加，有则累加票数
		try {
			select = in.readLine();
			if(!(map.containsKey(select))) {
				map.put(select,1);
			}else {
				map.put(select, map.get(select)+1);
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Map_Sort {
    // Map的value值降序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });
 
        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
 
    // Map的value值升序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortAscend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return compare;
            }
        });
 
        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
}

public class Sever_2 extends Application {
	static String vote[] = null;	
	static TextArea ta_info,ta_vote,ta_result;	
	static ServerSocket listenSocket = null;
	static Socket clientSocket = null;
	TextField tf_addr;
	Button btn_start,btn_stop;
    BufferedReader in = null;
    BufferedWriter out = null;
    Thread thread = null;
    Socket socket = null;
    
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
			
		GridPane pane_con = new GridPane();
		GridPane pane_vote = new GridPane();
		
		Label lb_addr = new Label("Server Addr:");
		tf_addr = new TextField("127.0.0.1:5000");
		tf_addr.setPrefColumnCount(15);
		btn_start = new Button("Start");
		btn_start.setOnAction(this::btnStartHandler);
		btn_stop = new Button("Stop");
		btn_stop.setOnAction(this::btnStopHandler);
		pane_con.addRow(0, lb_addr, tf_addr, btn_start,btn_stop);
		GridPane.setHalignment(lb_addr,HPos.RIGHT);
		pane_con.setAlignment(Pos.CENTER);
		pane_con.setHgap(20);
		pane_con.setVgap(10);
		pane_con.setMinSize(200, 60);
		
		Label lb_vote = new Label("Option:");
		Label lb_result = new Label("Result:");
		ta_vote = new TextArea();
		ta_result = new TextArea();
		ta_info = new TextArea();
		ta_info.setText("在Server Addr栏输入ip地址和端口号(以冒号隔开),\n"
				+ "在Option框中输入候选人(每人为一行),点击Start开始投票!\n");
		ta_vote.setPrefSize(200, 100);
		ta_result.setPrefSize(200, 100);
		pane_vote.setPrefSize(450, 150);
		pane_vote.setAlignment(Pos.CENTER);
		pane_vote.setHgap(20);
		pane_vote.setVgap(10);
		pane_vote.addRow(1,ta_vote,ta_result);
		pane_vote.addRow(0,lb_vote,lb_result);
		
		ta_info.setPrefSize(400, 100);
		ScrollPane pane_info = new ScrollPane(ta_info);		
		pane_info.setFitToWidth(true);
		
		ScrollPane pane_vote_2 = new ScrollPane(pane_vote);		
		pane_vote_2.setFitToWidth(false);
		
		TitledPane pane_01 = new TitledPane("Connect Border",pane_con);
		pane_01.setCollapsible(false);
		TitledPane pane_02 = new TitledPane("Vote Border",pane_vote_2);
		pane_02.setCollapsible(false);
		TitledPane pane_03 = new TitledPane("Information Border",pane_info);
		pane_03.setCollapsible(false);
		
		
		VBox box = new VBox(pane_01,pane_02,pane_03);
		box.setSpacing(10);
		box.setAlignment(Pos.CENTER);
		Scene scene = new Scene(box);
		stage.setScene(scene);
		stage.setTitle("Vote Sever 1740224125");
		stage.show();
	}
	
	public void btnStartHandler(ActionEvent event){
		//获取候选人并以\n为分隔符分开分别存储进数组vote
		vote = ta_vote.getText().split("/n");
		String addr[] = tf_addr.getText().split(":"); 
    	String host = addr[0];
    	int port = Integer.parseInt(addr[1]);
        try {
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress(host,port);
            listenSocket.bind(serverAddr); 
            ta_info.appendText("服务器启动成功！开始在localhost的5000端口侦听连接..."+"\n");
        	}catch (Exception e) {
        		// TODO: handle exception
        		e.printStackTrace();
        	}
        //调用监听线程
        thread = new Thread(new AcceptThread());
        thread.start();
        
        //控制按钮是否可操作
        btn_start.setDisable(true);
        btn_stop.setDisable(false);           
    }
	
	public void btnStopHandler(ActionEvent event){  
		//将获取到的地址与端口分离
		String addr[] = tf_addr.getText().split(":");
    	String host = addr[0];
    	listenSocket = null;
    	int port = Integer.parseInt(addr[1]);
    	
    	//设置结束accept标志
		try {
			thread.sleep(500);
			thread.interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//创建连接使thread跳过accept阻塞状态，使结束标志起作用
		socket = new Socket();
        SocketAddress remoteAddr=new InetSocketAddress(host, port);     
        try {
			socket.connect(remoteAddr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        btn_start.setDisable(true);
        btn_stop.setDisable(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch();
	}

}
