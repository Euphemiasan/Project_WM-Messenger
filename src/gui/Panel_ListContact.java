package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class Panel_ListContact extends JPanel
{
	private ArrayList<String> list_contact;
	
	private Project_WMMessenger program;
	
	private JLabel list_contact_title;
	private JScrollPane list_contact_scrollpane;
	private JList list_contact_jlist;

	public Panel_ListContact (Project_WMMessenger pwmm)
	{
		super();
		
		program = pwmm;
		list_contact = new ArrayList<String>();
		setPreferredSize(new Dimension(150, 400));
		setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		
		initComponent();
	}
	
	public void initComponent ()
	{
		list_contact_title = new JLabel(" Liste de Contacts");
		list_contact_title.setPreferredSize(new Dimension(150, 20));
		list_contact_title.setOpaque(true);
		list_contact_title.setBackground(new Color(50, 150, 200));
		add(list_contact_title);
		
		list_contact_jlist = new JList();
		list_contact_jlist.setBackground(new Color(175, 225, 255));
		list_contact_jlist.setSelectionBackground(new Color(135, 206, 235));
		
		list_contact_scrollpane = new JScrollPane(list_contact_jlist);
		Border border = BorderFactory.createEmptyBorder();  
		list_contact_scrollpane.setBorder(border);
		add(list_contact_scrollpane);

	}
	
	public void addContact (String contact)
	{
		list_contact.add(contact);
		
		refreshJList();
	}
	
	public void removeContact (String contact) 
	{
		ListIterator<String> it = list_contact.listIterator();
		while (it.hasNext())
		{
			String current_contact = it.next();
			
			if (current_contact.equals(contact))
				it.remove();
		}
		
		refreshJList();
	}
	
	public void refreshJList ()
	{
		DefaultListModel list_contact_jlist_model = new DefaultListModel();

		int taille = list_contact.size();

		for (int i=0; i<taille; i++)
		{
			list_contact_jlist_model.addElement(list_contact.get(i));
		}

		list_contact_jlist.removeAll();
		list_contact_jlist.setModel(list_contact_jlist_model);
	}
	
	public void removeJList ()
	{
		list_contact_jlist.removeAll();
	}

	public void refreshSize (int height)
	{
		setPreferredSize(new Dimension(150, height));
		list_contact_scrollpane.setPreferredSize(new Dimension(150, height-20));
	}
	
	public void printListContact ()
	{
		for (String contact : list_contact)
		{
			System.out.println(contact);
		}
	}
}
