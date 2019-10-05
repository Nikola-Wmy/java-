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
 * @Author: 李检辉
 * @Date: 2019/8/20
 * @version V1.0
 * @Description:基于TCP的客户端Socket，带用户界面窗口
 * @Project: 网络编程技术
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
    		 * 创建侦听套接字, 使用默认地址，绑定端口。
    		 * 操作系统将指向某个特定端口的入站连接请求存储在一个先进先出的队列中，不同系统队列长度会有所不同。
    		 * Java默认设置的队列长度为50，可以在创建ServerSocket时设置该值： 
    		 * 如果要指定连接accept队列的个数，可以应用　new ServerSocket(int port, int backlog),其中backlog为最大连接数
    		 * 如：当backlog=3时，如果服务器已经接收了一个客户请求并正与其通信，这时又有另外3个客户请求连接，进入了连接队列，
    		 * 此时如若再有其他客户来连接，则会被服务器拒绝，收到“Connection refused: connect”的异常
    		 */
        	
        	//1.启动服务器
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress("localhost",5000);
            listenSocket.bind(serverAddr); 
            System.out.println("服务器启动成功！开始在localhost的5000端口侦听连接...");
            //2.监听端口并接收客户端的连接请求        
            try {
                clientSocket = listenSocket.accept();
                System.out.println("客户机连接成功！客户机地址和端口："+clientSocket.getRemoteSocketAddress());
                //3.创建与与客户机会话 的输入输出流    
                in = new BufferedReader(
                     new InputStreamReader(
                     clientSocket.getInputStream()));           
                out = new BufferedWriter(
                      new OutputStreamWriter(            
                      clientSocket.getOutputStream()));
                //4.从客户机接收字符串
                String msg_recv=in.readLine(); 
                System.out.println("服务器收到字符串："+msg_recv);
                //5.向客户机回送字符串  
                out.write(msg_recv);           
                out.newLine();
                out.flush(); 
                System.out.println("服务器回送字符串成功："+msg_recv);
			} catch (Exception e) {
				System.out.println("异常信息"+e.getMessage());
			} finally {
		        //6.关闭与客户端通信的套接字和流
		        try {  
		            if (in != null)in.close();
		            if (out != null)  out.close();
		            if (clientSocket != null)   clientSocket.close(); 
		        } catch (IOException ex) {
		           System.out.println("异常信息"+ex.getMessage());
		        }
			}

        } catch (IOException ex) {
            System.out.println("异常信息："+ex.getMessage());
        }
        //7.关闭服务器套接字
        try {  
            if (listenSocket != null)   listenSocket.close();
        } catch (IOException ex) {
           System.out.println("异常信息"+ex.getMessage());
        }
	}

	public void btnDisconnectHandler(ActionEvent event){
        //6.关闭套接字和流
        try {
            if (in != null) in.close();
            if (out != null)  out.close();
            if (socket != null)   socket.close(); 
            System.out.println("关闭套接字和流成功！");
            btn_connect.setDisable(false);
            btn_disconnect.setDisable(true);
            ta_info.appendText(GuiUtils.getCurrentTimeString() + ":成功断开与服务器的连接\n");
        } catch (IOException ex) {
             System.out.println("异常信息："+ex.getMessage());
        } 
	}
	public void btnSendHandler(ActionEvent event){	
		String msg_sent = tf_msg.getText();
		
		 try {
            //4.向服务器发送字符串
            out.write(msg_sent); 
            out.newLine();
            out.flush();          
	        System.out.println(GuiUtils.getCurrentTimeString() + ":向服务器发送字符串成功!"+msg_sent);
	        ta_info.appendText(GuiUtils.getCurrentTimeString() + ":发送信息给服务器："+msg_sent+"\n");
	        
	        //5.从服务器接收字符串 
	        String msg_recv=in.readLine();  
	        System.out.println(GuiUtils.getCurrentTimeString() + ":从服务器接收回送字符串成功！"+msg_recv);
	        ta_info.appendText(GuiUtils.getCurrentTimeString() + ":从服务器接收信息："+msg_recv+"\n");
	        
        } catch (IOException ex) {
            System.out.println("异常信息："+ex.getMessage());
            GuiUtils.showAlert("连接服务器或发送信息出错！");
        }
	
	}
	public void TestInputStream() {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch();
	}

}
