package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Message;

public class Panel_Chat extends JTabbedPane implements ChangeListener
{
	private Project_WMMessenger program;
	
	private Panel_Broadcast panel_broadcast;
	private Panel_Conversation[] conversations;
	
	public Panel_Chat (Project_WMMessenger pwmm)
	{
		super(JTabbedPane.TOP);
		
		addChangeListener(this);
		
		program = pwmm;
		
		initComponent();
	}
	
	public void initComponent()
	{
		panel_broadcast = new Panel_Broadcast(program);
		addTab("Broadcast", null, panel_broadcast, null);
		
		conversations = new Panel_Conversation[0];
	}

	public void refreshSize (int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		revalidate();
	}

	public void addConversation (Panel_Conversation conversation)
	{
		// On creer un tableau temporaire
		Panel_Conversation[] conversations_tmp = new Panel_Conversation[conversations.length+1];
		
		// On transfert les donnees
		for (int i=0; i<conversations.length; i++)
		{
			conversations_tmp[i] = conversations[i];
		}
		
		// On ajoute la nouvelle conversation
		conversations_tmp[conversations_tmp.length-1] = conversation;
		conversations = conversations_tmp;
		
		if (conversation.getContacts().size() == 1){
			String nickname = Message.getNickname(conversation.getContacts().get(0));
			addTab(nickname, null, conversation, null);
		}
	}
	
	public int findConversation (ArrayList<String> contacts)
	{
		int index = -1;
		int i = -1;
		boolean checked;
		for (Panel_Conversation conversation : conversations)
		{
			i++;
			checked = true;
			if (conversation.getContacts().size() == contacts.size())
			{
				for (String contact : conversation.getContacts())
				{
					if (!contacts.contains(contact))
					{
						checked = false;
						break;
					}
				}
				if (checked)
				{
					index = i;
				}
			}
		}
		
		return index + 1;
	}
	
	public Panel_Broadcast getBroadcast ()
	{
		return panel_broadcast;
	}

	public void stateChanged (ChangeEvent ce)
	{
		setForegroundAt(getSelectedIndex(), Color.BLACK);
	}
}
