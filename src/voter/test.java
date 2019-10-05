package voter;
import java.awt.Checkbox;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.GuiUtils;

/**
 * @Author: ����
 * @Date: 2019/8/20
 * @version V1.0
 * @Description:����TCP�Ŀͻ���Socket�����û����洰��
 * @Project: �����̼���
 * @Copyright: All rights reserved
 */
public class test extends Application {

	TextField tf_msg, tf_addr;
	TextArea ta_info;
	
	Button btn_connect,btn_disconnect;
	Socket socket = null;
    /*BufferedReader in = null;
    BufferedWriter out = null;*/
    Socket clientSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    ServerSocket listenSocket = null;
    
    
    ComboBox<String> cBox = null;
    
    
    
    
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
			
		GridPane pane_com = new GridPane();
		Label lb_addr = new Label("Server Addr:");
		tf_addr = new TextField("127.0.0.1:5000");
		tf_addr.setPrefColumnCount(20);
		btn_connect = new Button("Connect");
		btn_connect.setOnAction(this::btnConnectHandler);
		btn_disconnect = new Button("Disconnect");
		btn_disconnect.setOnAction(this::btnDisconnectHandler);
		
		pane_com.addRow(0, lb_addr, tf_addr, btn_connect,btn_disconnect);
		GridPane.setHalignment(lb_addr,HPos.RIGHT);
		
		Label lb_msg = new Label("Message:");
		tf_msg = new TextField();
		tf_msg.setPrefColumnCount(20);
		Button btn_send = new Button("   Send  ");
		btn_send.setOnAction(this::btnSendHandler);
		
		
		
		
		
		cBox = new ComboBox<String>();
		cBox.getItems().addAll("GBK","UTF-8");
		cBox.setValue("UTF-8");
		
		
		
		
		pane_com.addRow(1, lb_msg, tf_msg,btn_send, cBox);
		GridPane.setHalignment(lb_msg,HPos.RIGHT);
		pane_com.setAlignment(Pos.CENTER);
		pane_com.setHgap(20);
		pane_com.setVgap(10);
		pane_com.setMinSize(200, 60);	
		TitledPane pane_01 = new TitledPane("Communication Border",pane_com);
		pane_01.setCollapsible(false);
		ta_info = new TextArea();
		//ta_info.setMinSize(500, 50);
		ScrollPane pane_info = new ScrollPane(ta_info);		
		pane_info.setFitToWidth(true);
		TitledPane pane_02 = new TitledPane("Information Border",pane_info);
		pane_02.setCollapsible(false);
		
		
		VBox box = new VBox(pane_01,pane_02);
		box.setSpacing(10);
		box.setAlignment(Pos.CENTER);
		Scene scene = new Scene(box);
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.show();
	}
	
	public void btnConnectHandler(ActionEvent event){

        try {
    		/*
    		 * ���������׽���, ʹ��Ĭ�ϵ�ַ���󶨶˿ڡ�
    		 * ����ϵͳ��ָ��ĳ���ض��˿ڵ���վ��������洢��һ���Ƚ��ȳ��Ķ����У���ͬϵͳ���г��Ȼ�������ͬ��
    		 * JavaĬ�����õĶ��г���Ϊ50�������ڴ���ServerSocketʱ���ø�ֵ�� 
    		 * ���Ҫָ������accept���еĸ���������Ӧ�á�new ServerSocket(int port, int backlog),����backlogΪ���������
    		 * �磺��backlog=3ʱ������������Ѿ�������һ���ͻ�����������ͨ�ţ���ʱ��������3���ͻ��������ӣ����������Ӷ��У�
    		 * ��ʱ�������������ͻ������ӣ���ᱻ�������ܾ����յ���Connection refused: connect�����쳣
    		 */
        	
        	//1.����������
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress("localhost",5000);
            listenSocket.bind(serverAddr); 
            System.out.println("�����������ɹ�����ʼ��localhost��5000�˿���������...");
            //2.�����˿ڲ����տͻ��˵���������        
            try {
                clientSocket = listenSocket.accept();
                System.out.println("�ͻ������ӳɹ����ͻ�����ַ�Ͷ˿ڣ�"+clientSocket.getRemoteSocketAddress());
                //3.��������ͻ����Ự �����������    
                in = new BufferedReader(
                     new InputStreamReader(
                     clientSocket.getInputStream()));           
                out = new BufferedWriter(
                      new OutputStreamWriter(            
                      clientSocket.getOutputStream()));
                //4.�ӿͻ��������ַ���
                String msg_recv=in.readLine(); 
                System.out.println("�������յ��ַ�����"+msg_recv);
                //5.��ͻ��������ַ���  
                out.write(msg_recv);           
                out.newLine();
                out.flush(); 
                System.out.println("�����������ַ����ɹ���"+msg_recv);
			} catch (Exception e) {
				System.out.println("�쳣��Ϣ"+e.getMessage());
			} finally {
		        //6.�ر���ͻ���ͨ�ŵ��׽��ֺ���
		        try {  
		            if (in != null)in.close();
		            if (out != null)  out.close();
		            if (clientSocket != null)   clientSocket.close(); 
		        } catch (IOException ex) {
		           System.out.println("�쳣��Ϣ"+ex.getMessage());
		        }
			}

        } catch (IOException ex) {
            System.out.println("�쳣��Ϣ��"+ex.getMessage());
        }
        //7.�رշ������׽���
        try {  
            if (listenSocket != null)   listenSocket.close();
        } catch (IOException ex) {
           System.out.println("�쳣��Ϣ"+ex.getMessage());
        }
	}

	public void btnDisconnectHandler(ActionEvent event){
        //6.�ر��׽��ֺ���
        try {
            if (in != null) in.close();
            if (out != null)  out.close();
            if (socket != null)   socket.close(); 
            System.out.println("�ر��׽��ֺ����ɹ���");
            btn_connect.setDisable(false);
            btn_disconnect.setDisable(true);
            ta_info.appendText(GuiUtils.getCurrentTimeString() + ":�ɹ��Ͽ��������������\n");
        } catch (IOException ex) {
             System.out.println("�쳣��Ϣ��"+ex.getMessage());
        } 
	}
	public void btnSendHandler(ActionEvent event){	
		String msg_sent = tf_msg.getText();
		
		 try {
            //4.������������ַ���
            out.write(msg_sent); 
            out.newLine();
            out.flush();          
	        System.out.println(GuiUtils.getCurrentTimeString() + ":������������ַ����ɹ�!"+msg_sent);
	        ta_info.appendText(GuiUtils.getCurrentTimeString() + ":������Ϣ����������"+msg_sent+"\n");
	        
	        //5.�ӷ����������ַ��� 
	        String msg_recv=in.readLine();  
	        System.out.println(GuiUtils.getCurrentTimeString() + ":�ӷ��������ջ����ַ����ɹ���"+msg_recv);
	        ta_info.appendText(GuiUtils.getCurrentTimeString() + ":�ӷ�����������Ϣ��"+msg_recv+"\n");
	        
        } catch (IOException ex) {
            System.out.println("�쳣��Ϣ��"+ex.getMessage());
            GuiUtils.showAlert("���ӷ�����������Ϣ����");
        }
	
	}
	public void TestInputStream() {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch();
	}

}
