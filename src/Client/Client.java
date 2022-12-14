package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener{
	private JButton send,clear,exit,login,logout;
    private JPanel p_login,p_chat;
    private JTextField nick,nick1,message;
    private JTextArea msg,online;
    
    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
	private DataInputStream dis;
    
	public Client(){
		super("chatting: Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}	
		});
		setSize(600, 400);
		addItem();
		setVisible(true);
	}
//-----[ create client ]--------//
	private void addItem() {
		this.getContentPane().setBackground(new Color(0,32,63));
		setLayout(new BorderLayout());
		
		exit = new JButton("exit");
		exit.addActionListener(this);
		send = new JButton("send");
		send.addActionListener(this);
		clear = new JButton("delete");
		clear.addActionListener(this);
		login= new JButton("login");
		login.addActionListener(this);
		logout= new JButton("exit");
		logout.addActionListener(this);
		
		p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());
		
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.CENTER));
		nick = new JTextField(20);
		p1.add(new JLabel("User name : "));
		nick.setBackground(new Color(0,32,63));
		nick.setFont(new Font("Serif", Font.PLAIN, 14));
		nick.setForeground(new Color(173,239,209));
		p1.add(nick);
		p1.add(exit);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		
		JPanel p22 = new JPanel();
		p22.setLayout(new FlowLayout(FlowLayout.CENTER));
		p22.add(new JLabel("Online list "));
		p2.add(p22,BorderLayout.NORTH);
		online = new JTextArea(10,10);
		online.setBackground(new Color(0,32,63));
		online.setFont(new Font("Serif", Font.PLAIN, 14));
		online.setForeground(new Color(173,239,209));
		online.setEditable(false);
		p2.add(new JScrollPane(online),BorderLayout.CENTER);
		p2.add(new JLabel("     "),BorderLayout.SOUTH);
		p2.add(new JLabel("     "),BorderLayout.EAST);
		p2.add(new JLabel("     "),BorderLayout.WEST);
		
		msg = new JTextArea(10,20);
		msg.setBackground(new Color(0,32,63));
		msg.setFont(new Font("Serif", Font.PLAIN, 14));
		msg.setForeground(new Color(173,239,209));
		msg.setEditable(false);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.CENTER));
		p3.add(new JLabel("message"));
		message = new JTextField(30);
		message.setBackground(new Color(0,32,63));
		message.setFont(new Font("Serif", Font.PLAIN, 14));
		message.setForeground(new Color(173,239,209));
		p3.add(message);
		p3.add(send);
		p3.add(clear);
		
		p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
		p_chat.add(p1,BorderLayout.NORTH);
		p_chat.add(p2,BorderLayout.EAST);
		p_chat.add(p3,BorderLayout.SOUTH);
		p_chat.add(new JLabel("     "),BorderLayout.WEST);
		
		p_chat.setVisible(false);
		add(p_chat,BorderLayout.CENTER);
		
		//-------------------------
		p_login = new JPanel();
		p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel("User name: ");
		label.setForeground(new Color(173,239,209));
		p_login.add(label);
		nick1=new JTextField(20);
		nick1.setFont(new Font("Serif", Font.PLAIN, 14));
		
		p_login.add(nick1);
		p_login.add(login);
		p_login.add(logout);
		p_login.setBackground(new Color(0,32,63));
		p_login.setForeground(new Color(173,239,209));
		add(p_login,BorderLayout.NORTH);
	}
//---------[ Socket ]-----------//	
	private void go() {
		try {
			client = new Socket("localhost",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());
		
			//client.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"error, please checking your server","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		new Client().go();
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		// other message
		case 3:
			this.msg.append(msg2);
			break;
		// updating online list
		case 4:
			this.online.setText(msg2);
			break;
		// close server
		case 5:
			dataStream.stopThread();
			exit();
			break;
		
		default:
			break;
		}
	}
//----------------------------------------------
	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("you: "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	private boolean checkLogin(String nick){
		if(nick.compareTo("")==0)
			return false;
		else if(nick.compareTo("0")==0){
			return false;
		}
		else{
			sendMSG(nick);
			int sst = Integer.parseInt(getMSG());
			if(sst==0)
				 return false;
			else return true;
		}
	}

	private void exit(){
		try {
			sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
			dataStream.stopThread();
			exit();
		}
		else if(e.getSource()==clear){
			message.setText("");
		}
		else if(e.getSource()==send){
			checkSend(message.getText()+"\n");
			message.setText("");
		}
		else if(e.getSource()==login){
			if(checkLogin(nick1.getText())){
				p_chat.setVisible(true);
				p_login.setVisible(false);
				nick.setText(nick1.getText());
				nick.setEditable(false);
				this.setTitle(nick1.getText());
				msg.append("loggin succes!\n");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Username already exists, please login again","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			exit();
		}
	}


}
