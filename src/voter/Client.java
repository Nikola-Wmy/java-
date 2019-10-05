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

public class Client extends Application {

	TextField tf_addr;
	TextArea ta_info,ta_vote,ta_voter;
	
	Button btn_start,btn_stop,btn_vote;
	Socket socket = null;
//    BufferedReader in = null;
//    BufferedWriter out = null;
    ComboBox<String> cBox = null;
    Socket clientSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    ServerSocket listenSocket = null;
	
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
		btn_start = new Button("Connect");
		btn_start.setOnAction(this::btnConnectHandler);
		btn_stop = new Button("Disconnect");
		pane_con.addRow(0, lb_addr, tf_addr, btn_start,btn_stop);
		GridPane.setHalignment(lb_addr,HPos.RIGHT);
		pane_con.setAlignment(Pos.CENTER);
		pane_con.setHgap(20);
		pane_con.setVgap(10);
		pane_con.setMinSize(200, 60);
		
		Label lb_vote = new Label("Information:");
		Label lb_voter = new Label("Option:");
		ta_vote = new TextArea();
		ComboBox<String> cBox = new ComboBox();
		cBox.setPrefWidth(150);

		ta_vote.setPrefSize(200, 100);
		btn_vote = new Button("               Vote               ");

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
		pane_voter.addRow(7, btn_vote);
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
		try {
            listenSocket = new ServerSocket();
            SocketAddress serverAddr=new InetSocketAddress("localhost",5000);
            listenSocket.bind(serverAddr); 
            System.out.println("�����������ɹ�����ʼ��localhost��5000�˿���������...");
                                           
            while (true){
            	try {
            		//2.�����˿ڲ����տͻ��˵���������  
		            clientSocket = listenSocket.accept();
		            System.out.println("�ͻ������ӳɹ����ͻ�����ַ�Ͷ˿ڣ�"+clientSocket.getRemoteSocketAddress());
		            //3.��ͻ����Ự           
		            in = new BufferedReader(
		                 new InputStreamReader(
		                 clientSocket.getInputStream(),"utf-8"));           
		            out = new BufferedWriter(
		                  new OutputStreamWriter(            
		                  clientSocket.getOutputStream(),"utf-8"));
		            String recv_msg="";  
		            //4.�ӿͻ��������ַ���
		            while((recv_msg=in.readLine())!=null){
	
			            System.out.println("�������յ��ַ�����"+recv_msg);
			            //5.��ͻ��������ַ��� 
			            out.write(recv_msg);         
			            out.newLine();
			            out.flush(); 
			            System.out.println("�����������ַ����ɹ���"+recv_msg);
			            
		            	if(recv_msg.equals("bye")){
		            		break;
		            	}
		            }
	            } catch (IOException ex) {
		               System.out.println("�쳣��Ϣ"+ex.getMessage());
		        } finally {
		            //6.�ر��׽��ֺ���
		            try {  
		                if (in != null) in.close();
		                if (out != null)  out.close();
		                if (clientSocket != null)   clientSocket.close(); 
		            } catch (IOException ex) {
		               System.out.println("�쳣��Ϣ"+ex.getMessage());
		            }
				}
            }
        } catch (IOException ex) {
            System.out.println("�쳣��Ϣ��"+ex.getMessage());
        }
        //7.�ر��׽��ֺ���
        try {  
            if (listenSocket != null)   listenSocket.close();
        } catch (IOException ex) {
           System.out.println("�쳣��Ϣ"+ex.getMessage());
        }
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
	}
	/*public void btnDisconnectHandler(ActionEvent event){
        //6.�ر��׽��ֺ���
        try {
            if (in != null) in.close();
            if (out != null)  out.close();
            if (socket != null)   socket.close(); 
            System.out.println("�ر��׽��ֺ����ɹ���");
            btn_connect.setDisable(false);
            btn_disconnect.setDisable(true);
            ta_info.appendText( ":�ɹ��Ͽ��������������\n");
        } catch (IOException ex) {
             System.out.println("�쳣��Ϣ��"+ex.getMessage());
        } 
	}*/
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

