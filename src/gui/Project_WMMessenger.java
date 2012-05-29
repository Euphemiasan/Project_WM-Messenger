package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Cast;
import core.Message;

public class Project_WMMessenger extends JFrame
{
	private Cast cast;
	private JPanel content;

	private JDialog_Connexion dialog_connexion;
	
	private Panel_ListContact panel_list_contact;
	private Panel_Chat panel_chat;
	
	private String nickname;
	private ArrayList<String> list_nickname;
	
	public Project_WMMessenger()
	{
		super();
		
		cast = new Cast(this);
		nickname = "";
		list_nickname = new ArrayList<String>();
		
		setTitle("WM-Messenger v1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		content = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				resizeContent();
			}
		};
		content.setPreferredSize(new Dimension(600, 400));
		content.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		setContentPane(content);
		
		WMMessengerListener program_listener = new WMMessengerListener(this);
		addWindowListener(program_listener);
		
		pack();
		
		// Positionne la fenetre au milieu de l'ecran
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int width = getSize().width;
		int height = getSize().height;
		int x = (dimension.width - width) / 2;
		int y = (dimension.height - height) / 2;
		setLocation(x, y);
		
		
		
		initComponent();
		
		//Message message = new Message(cast.getAddress().toString(), null, 1, null);
		//cast.sendBroadcast(message);
		
		
		//System.out.println(Message.getNickname("192.168.1.9:40000;robert"));
		//printListNickname();
		//panel_list_contact.printListContact();
	}
	
	public void initComponent ()
	{
		panel_list_contact = new Panel_ListContact(this);
		content.add(panel_list_contact);
		
		panel_chat = new Panel_Chat(this);
		content.add(panel_chat);
		
		resizeContent();
		
		dialog_connexion = new JDialog_Connexion(this, "Connexion", true);
	}
	
	public void changeTitle (String ip, String nickname)
	{
		setTitle("WM-Messenger v1.0 | " + ip + " | " + nickname);
	}
	
	public void resizeContent ()
	{
		panel_list_contact.refreshSize(content.getSize().height);

		panel_chat.refreshSize(content.getSize().width - panel_list_contact.getWidth(), content.getSize().height);
	}
	
	public Panel_ListContact getListContact ()
	{
		return panel_list_contact;
	}
	
	public String getNickname ()
	{
		return nickname;
	}
	
	public void addNickname (String nickname)
	{
		list_nickname.add(nickname);
	}
	
	public boolean availableNickname ()
	{
		for (String nick : list_nickname)
		{
			if (nick.equals(nickname))
				return false;
		}
		
		return true;
	}

	public void printListNickname ()
	{
		for (String nick : list_nickname)
		{
			System.out.println(nick);
		}
	}
	
	// Methode principale du programme
	public static void main (String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Project_WMMessenger messenger = new Project_WMMessenger();
					messenger.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
