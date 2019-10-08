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
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.GuiUtils;

public class Client_2 extends Application {

	TextField tf_addr;
	TextArea ta_vote;
	
	Button btn_connect,btn_disconnect,btn_vote,btn_get;
	Socket socket = null;
    ComboBox<String> cBox = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
			
		GridPane pane_con = new GridPane();
		GridPane pane_vote = new GridPane();
		GridPane pane_option = new GridPane();
		GridPane pane_voter = new GridPane();
		
		Label lb_addr = new Label("Server Addr:");
		tf_addr = new TextField("127.0.0.1:5000");
		tf_addr.setPrefColumnCount(10);
		btn_connect = new Button("Connect");
		btn_connect.setOnAction(this::btnConnectHandler);
		btn_disconnect = new Button("Disconnect");
		btn_disconnect.setDisable(true);
		btn_disconnect.setOnAction(this::btnDisconnectHandler);
		pane_con.addRow(0, lb_addr, tf_addr, btn_connect,btn_disconnect);
		GridPane.setHalignment(lb_addr,HPos.RIGHT);
		pane_con.setAlignment(Pos.CENTER);
		pane_con.setHgap(20);
		pane_con.setVgap(10);
		pane_con.setMinSize(200, 60);
		
		Label lb_vote = new Label("Information:");
		Label lb_voter = new Label("Option:");
		ta_vote = new TextArea();
		cBox = new ComboBox();
		cBox.setPrefWidth(150);
		cBox.setValue("候选人");

		ta_vote.setPrefSize(200, 100);
		ta_vote.setText("在Option框中选择候选人,点击Vote\n完成投票,\n"			
				+ "在服务器终止投票后点击Get获取投\n票情况");
		btn_vote = new Button("               Vote               ");
		btn_get = new Button("                Get               ");
		btn_vote.setOnAction(this::btnVoteHandler);
		btn_get.setOnAction(this::btnGetHandler);
		btn_vote.setDisable(true);
		btn_get.setDisable(true);
		pane_vote.setPrefSize(500, 150);
		pane_vote.setAlignment(Pos.CENTER);
		pane_vote.setHgap(50);
		pane_vote.setVgap(10);
		GridPane.setHalignment(cBox,HPos.RIGHT);
		
		pane_vote.addRow(0,pane_option,pane_voter);
		pane_option.addRow(0,lb_vote);
		pane_option.addRow(1,ta_vote);
		pane_voter.addRow(0, lb_voter);
		pane_voter.addRow(1, cBox);
		pane_voter.addRow(5, btn_get);
		pane_voter.addRow(3, btn_vote);
		pane_voter.setVgap(8.5);
		pane_option.setVgap(8.5);
				
		ScrollPane pane_vote_2 = new ScrollPane(pane_vote);		
		pane_vote_2.setFitToWidth(false);
		
		TitledPane pane_01 = new TitledPane("Connect Border",pane_con);
		pane_01.setCollapsible(false);
		TitledPane pane_02 = new TitledPane("Vote Border",pane_vote_2);
		pane_02.setCollapsible(false);
				
		VBox box = new VBox(pane_01,pane_02);
		box.setSpacing(10);
		box.setAlignment(Pos.CENTER);
		Scene scene = new Scene(box);
		stage.setScene(scene);
		stage.setTitle("Vote Client 1740224125");
		stage.show();
	}
	
	public void btnConnectHandler(ActionEvent event){
		String s = null;		
        try {
        	String addr[] = tf_addr.getText().split(":");
        	String host_name = addr[0];        	
        	int port = Integer.parseInt(addr[1]);
        	socket = new Socket();
        	socket.setSoTimeout(1*1000);
            SocketAddress remoteAddr=new InetSocketAddress(host_name, port); 
            System.out.println("创建客户机套接字成功！");
            socket.connect(remoteAddr);
            System.out.println("客户机连接服务器成功！");
            System.out.println("客户机使用的地址和端口："+socket.getLocalSocketAddress());
            in = new BufferedReader(
                 new InputStreamReader(
                		 socket.getInputStream(),"utf-8"));           
            out = new BufferedWriter(
                  new OutputStreamWriter(            
                		  socket.getOutputStream(),"utf-8"));
            btn_connect.setDisable(true);
            btn_disconnect.setDisable(false);
            ta_vote.setText("候选人名单如下:\n");
            cBox.getItems().clear();
            cBox.setValue("候选人");
            while((s = in.readLine()) != null) {
            	if (s.equals("end")) {
            		break;
            	}
            	ta_vote.appendText(s+"\n");
            	cBox.getItems().addAll(s);            	
            }

        } catch (IOException ex) {
            System.out.println("异常信息："+ex.getMessage());
            GuiUtils.showAlert("连接服务器出错！");
        }
        
        btn_vote.setDisable(false);
        btn_get.setDisable(true);
        btn_connect.setDisable(true);
	}
	
	public void btnVoteHandler(ActionEvent event){
		try {
			out.write(cBox.getValue()); 
            out.newLine();
            out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btn_vote.setDisable(true);
        btn_get.setDisable(false);
        btn_connect.setDisable(true);
        btn_disconnect.setDisable(true);
	}
	
	public void btnGetHandler(ActionEvent event){
		String s = null;
		btn_vote.setDisable(true);
	    btn_get.setDisable(true);
	    btn_connect.setDisable(true);
		ta_vote.setText("");
			try {
        		s=in.readLine();
        		ta_vote.appendText(s.replace(";","票!\n"));
        		System.out.println(s);
	
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "投票还未结束!");
				btn_get.setDisable(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			        
	}

	public void btnDisconnectHandler(ActionEvent event){
        //6.关闭套接字和流
        try {
            if (in != null) in.close();
            if (out != null)  out.close();
            if (socket != null)   socket.close(); 
            //System.out.println("关闭套接字和流成功！");
            btn_connect.setDisable(false);
            btn_disconnect.setDisable(true);            
        } catch (IOException ex) {
             System.out.println("异常信息："+ex.getMessage());
        } 
        btn_vote.setDisable(true);
        btn_get.setDisable(true);
        btn_connect.setDisable(false);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch();
		
	}

}

