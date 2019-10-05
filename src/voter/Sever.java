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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Sever extends Application {

	TextField tf_addr;
	TextArea ta_info,ta_vote,ta_result;
	
	Button btn_start,btn_stop;
	ServerSocket listenSocket = null;
	Socket clientSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    Thread thread = new Thread();

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
		//1.����������
        
        try {
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress("localhost",5000);
            listenSocket.bind(serverAddr); 
            System.out.println("�����������ɹ�����ʼ��localhost��5000�˿���������...");
        	}catch (Exception e) {
        		// TODO: handle exception
        		e.printStackTrace();
        	}
        
        Thread thread =  new Thread(new Runnable() {
			public void run() {
				
					while(true) {
						try {
						clientSocket = listenSocket.accept();
					    System.out.println("�ͻ������ӳɹ����ͻ�����ַ�Ͷ˿ڣ�"+clientSocket.getRemoteSocketAddress());
					    in = new BufferedReader(
				                 new InputStreamReader(
				                 clientSocket.getInputStream(),"utf-8"));           
				        out = new BufferedWriter(
				                  new OutputStreamWriter(            
				                  clientSocket.getOutputStream(),"utf-8"));
				        String recv_msg="";
				        while((recv_msg=in.readLine())!=null){
					            System.out.println("�������յ��ַ�����"+recv_msg);
					            //5.��ͻ��������ַ��� 
					            out.write(recv_msg);         
					            out.newLine();
					            out.flush(); 
					            System.out.println("�����������ַ����ɹ���"+recv_msg);
				            }
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
				
			}
		});
        
        thread.start();
        btn_start.setDisable(true);
        btn_stop.setDisable(false);
		              
    }
	
	public void btnStopHandler(ActionEvent event){
        //6.�ر��׽��ֺ���
		try {  
            if (in != null) in.close();
            if (out != null)  out.close();
            if (clientSocket != null)   clientSocket.close(); 
        } catch (IOException ex) {
           System.out.println("�쳣��Ϣ"+ex.getMessage());
        }
		try {
			thread.sleep(200);
		}catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        btn_start.setDisable(false);
        btn_stop.setDisable(true);
	}
	
	/*public void btnSendHandler(ActionEvent event){	
		String msg_sent = tf_msg.getText();
		
		 try {
            //4.������������ַ���
            out.write(msg_sent); 
            out.newLine();
            out.flush();          
	        System.out.println(":������������ַ����ɹ�!"+msg_sent);
	        ta_info.appendText(":������Ϣ����������"+msg_sent+"\n");
	        
	        //5.�ӷ����������ַ��� 
	        String msg_recv=in.readLine();  
	        System.out.println( ":�ӷ��������ջ����ַ����ɹ���"+msg_recv);
	        ta_info.appendText(":�ӷ�����������Ϣ��"+msg_recv+"\n");
	        
        } catch (IOException ex) {
            System.out.println("�쳣��Ϣ��"+ex.getMessage());
            
        }
	
	}*/
	
	/*public void UploadfileHandler(ActionEvent event){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please select a file");
		File f = fileChooser.showOpenDialog(null);
		if(f !=null){
			//��Ӵ��룺���ļ�file���ݷ��͸�������
			try {
		        Socket s =new Socket("localhost",8000);

		        //�����׽�����������Է������ݸ�������
		        DataOutputStream out=new DataOutputStream(
		                             new BufferedOutputStream(
		                             s.getOutputStream()));
		        //�����׽��������������շ�����������Ϣ
		        BufferedReader br=new BufferedReader(
		                          new InputStreamReader(
		                          s.getInputStream()));
		        //�����ļ�������
		        DataInputStream in=new DataInputStream(
		                           new BufferedInputStream(
		                           new FileInputStream(f)));
		        long fileLen=f.length();  //�����ļ�����
		        //�����ļ����ơ��ļ�����
		        out.writeUTF(f.getName());
		        out.writeLong(fileLen);
		        out.flush();
		        //�����ļ�����
		        int numRead=0; //���ζ�ȡ���ֽ���
		        int numFinished=0; //������ֽ���
		        byte[] buffer=new byte[8096];   
		        while (numFinished<fileLen && (numRead=in.read(buffer))!=-1) { //�ļ��ɶ�  
		            out.write(buffer,0,numRead);  //����
		            out.flush();    
		            numFinished+=numRead; //������ֽ���
		        }//end while
		        in.close(); 

		        String response=br.readLine();//��ȡ���ش�        
		        if (response.equalsIgnoreCase("M_DONE")) { //�������ɹ�����               
		            System.out.println(f.getName() +"  ���ͳɹ���");
		            ta_info.appendText(f.getName() +"  ���ͳɹ���");
		        }else if (response.equalsIgnoreCase("M_LOST")){ //����������ʧ��
		        	System.out.println(f.getName() +"  ����ʧ�ܣ�") ;
		        	ta_info.appendText(f.getName() +"  ����ʧ�ܣ�");
		        }//end if
		        //�ر���
		        br.close();
		        out.close();
		        s.close();
			}catch (Exception i) {
				// TODO: handle exception
				i.printStackTrace();
			}	
		}
	}*/
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch();
	}

}
