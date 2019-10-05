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
	//�洢��������������ӵ�socke�����ڷ���ͶƱ���
	ArrayList<Socket> socketlist = new ArrayList<Socket>();
	Socket clientSocket = null;
	ServerSocket listenSocket = Sever_2.listenSocket;
	String vote [] = Sever_2.vote;
	ArrayList<String> temp  = new ArrayList<String>();
	
	public void run() {
		//����accept��������
		while(true) {			
			try {
				clientSocket = listenSocket.accept();
				socketlist.add(clientSocket);
				new Thread(new ServerThread_2(clientSocket)).start();			
				Sever_2.ta_info.appendText("�ͻ������ӳɹ����ͻ�����ַ�Ͷ˿ڣ�"+clientSocket.getRemoteSocketAddress()+"\n");
		    BufferedWriter out = null;
		    out = new BufferedWriter(
	                 new OutputStreamWriter(            
	                 clientSocket.getOutputStream(),"utf-8"));
		    
		    //��ͻ��˷��ͺ�ѡ��
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
		   
			//ֹͣ������־
			if(this.interrupted()) {			
					break;
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}			
		}
		
		//������Map_Sort�ĵ������򷨣�����ѡ�˰�Ʊ����������
		ServerThread_2.map = Map_Sort.sortDescend(ServerThread_2.map);
		
		//��ͻ��˷���ͶƱ���
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
		
		//��result�����ͶƱ���
		Sever_2.ta_result.setText("");
		for(String key:ServerThread_2.map.keySet()) {
			Integer value = ServerThread_2.map.get(key);
			Sever_2.ta_result.appendText(key+":"+value+" Ʊ!\n");
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
		
		//����I/O��
		in = new BufferedReader(
                new InputStreamReader(
                s.getInputStream(),"utf-8"));           
        out = new BufferedWriter(
                 new OutputStreamWriter(            
                 s.getOutputStream(),"utf-8"));      
	}
	
	//synchronized ��֤�ô������ͬһʱ��ֻ����һ���߳�����
	synchronized public void run()
	{
		//���map���Ƿ��иú�ѡ�ˣ�û������ӣ������ۼ�Ʊ��
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
    // Map��valueֵ��������
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
 
    // Map��valueֵ��������
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
		ta_info.setText("��Server Addr������ip��ַ�Ͷ˿ں�(��ð�Ÿ���),\n"
				+ "��Option���������ѡ��(ÿ��Ϊһ��),���Start��ʼͶƱ!\n");
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
		//��ȡ��ѡ�˲���\nΪ�ָ����ֿ��ֱ�洢������vote
		vote = ta_vote.getText().split("/n");
		String addr[] = tf_addr.getText().split(":"); 
    	String host = addr[0];
    	int port = Integer.parseInt(addr[1]);
        try {
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress(host,port);
            listenSocket.bind(serverAddr); 
            ta_info.appendText("�����������ɹ�����ʼ��localhost��5000�˿���������..."+"\n");
        	}catch (Exception e) {
        		// TODO: handle exception
        		e.printStackTrace();
        	}
        //���ü����߳�
        thread = new Thread(new AcceptThread());
        thread.start();
        
        //���ư�ť�Ƿ�ɲ���
        btn_start.setDisable(true);
        btn_stop.setDisable(false);           
    }
	
	public void btnStopHandler(ActionEvent event){  
		//����ȡ���ĵ�ַ��˿ڷ���
		String addr[] = tf_addr.getText().split(":");
    	String host = addr[0];
    	listenSocket = null;
    	int port = Integer.parseInt(addr[1]);
    	
    	//���ý���accept��־
		try {
			thread.sleep(500);
			thread.interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��������ʹthread����accept����״̬��ʹ������־������
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
