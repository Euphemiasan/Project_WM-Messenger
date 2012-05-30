package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Cast;
import core.Message;

public class Project_WMMessenger extends JFrame
{
	private static final long serialVersionUID = 1L;
	
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
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				resizeContent();
			}
		};
		content.setPreferredSize(new Dimension(600, 425));
		content.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		setContentPane(content);
		
		WindowListener program_listener = new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				String my_contact = Cast.getAddress() + ";" + nickname;
				Message message = new Message(my_contact, null, 3, null);
				
				cast.sendBroadcast(message);
			}
		};
		addWindowListener(program_listener);
		
		pack();
		setMinimumSize(new Dimension(getWidth(), getHeight()));
		
		// Positionne la fenetre au milieu de l'ecran
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int width = getSize().width;
		int height = getSize().height;
		int x = (dimension.width - width) / 2;
		int y = (dimension.height - height) / 2;
		setLocation(x, y);

		Message message = new Message(Cast.getAddress(), null, 1, null);
		cast.sendBroadcast(message);

		initComponent();
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
	
	public Panel_Chat getChat ()
	{
		return panel_chat;
	}
	
	public Cast getCast ()
	{
		return cast;
	}
	
	public String getNickname ()
	{
		return nickname;
	}
	
	public void setNickname (String nick)
	{
		nickname = nick;
	}
	
	public void addNickname (String nickname)
	{
		list_nickname.add(nickname);
	}
	
	public boolean availableNickname (String test_nickname)
	{
		for (String nick : list_nickname)
		{
			if (nick.equals(test_nickname))
				return false;
		}
		
		return true;
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
