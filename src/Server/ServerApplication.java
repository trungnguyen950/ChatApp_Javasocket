package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerApplication extends JFrame implements ActionListener{
	
	private JButton close;
	public JTextArea user;
	private ServerSocket server;
	public Hashtable<String, ClientConnect> listUser;
	
	public ServerApplication(){
		super("Chatting : Server");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					//sending message to all client
					server.close();
					System.exit(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
		});
		setSize(400, 400);
		addItem();
		setVisible(true);
	}
	
	private void addItem() {
		setLayout(new BorderLayout());
		
		add(new JLabel("server status : \n"),BorderLayout.NORTH);
		add(new JPanel(),BorderLayout.EAST);
		add(new JPanel(),BorderLayout.WEST);
		
		user = new JTextArea(10,20);
		user.setBackground(new Color(0,32,63));
		user.setFont(new Font("Serif", Font.PLAIN, 14));
		user.setForeground(new Color(173,239,209));;
		user.setEditable(false);
		add(new JScrollPane(user),BorderLayout.CENTER);
		
		close = new JButton("Close Server");
		close.addActionListener(this);
		add(close,BorderLayout.SOUTH);

		user.append("sever is openning \n");
	}
	
	private void go(){
		try {
			listUser = new Hashtable<String, ClientConnect>();
			server = new ServerSocket(2207);
			user.append("sever is online now! \n");
			while(true){
				Socket client = server.accept();
				new ClientConnect(this,client);
			}
		} catch (IOException e) {
			user.append("server can not connect! \n");
		}
	}
	
	public static void main(String[] args) {
		new ServerApplication().go();
	}

	public void actionPerformed(ActionEvent e) {
			try {
				server.close();
			} catch (IOException e1) {
				user.append("server can not stop! \n");
			}
			System.exit(0);
	}
	
	public void sendAll(String from, String msg){
		Enumeration e = listUser.keys();
		String name=null;
		while(e. hasMoreElements()){
			name=(String) e.nextElement();
			
			if(name.compareTo(from)!=0) listUser.get(name).sendMSG("3",msg);
		}
	}
	public void sendAllUpdate(String from){
		Enumeration e = listUser.keys();
		String name=null;
		while(e. hasMoreElements()){
			name=(String) e.nextElement();
			
			if(name.compareTo(from)!=0) listUser.get(name).sendMSG("4",getAllName());
		}
	}
	
	public String getAllName(){
		Enumeration e = listUser.keys();
		String name="";
		while(e. hasMoreElements()){
			name+=(String) e.nextElement()+"\n";
		}
		return name;
	}

}
