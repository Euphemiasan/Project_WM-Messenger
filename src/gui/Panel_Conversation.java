package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.Cast;
import core.Message;

public class Panel_Conversation extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private Project_WMMessenger program;

	private JTextArea ucast_log;
	private JScrollPane ucast_log_pane;

	private JTextArea ucast_chat;
	private JScrollPane ucast_chat_pane;

	private ArrayList<String> contacts;
	private JScrollPane ucast_recipient_pane;
	private JList ucast_recipient;
	
	private JButton ucast_send_message;
	private JButton ucast_send_file;

	private JFileChooser file_chooser;
	private String file_name;
	
	
	public Panel_Conversation (Project_WMMessenger pwmm, ArrayList<String> cons)
	{
		program = pwmm;
		contacts = cons;
		
		setPreferredSize(new Dimension(483, 426));
		setBackground(new Color(200, 221, 242));
		
		initComponent();
	}
	
	public void initComponent ()
	{
		GridBagLayout bcast_layout = new GridBagLayout();
		setLayout(bcast_layout);
		
		GridBagConstraints layout_rules = new GridBagConstraints();
		layout_rules.fill = GridBagConstraints.BOTH;
		layout_rules.weightx = 0;
		layout_rules.weighty = 0;
		layout_rules.anchor = GridBagConstraints.CENTER;
		
		ucast_log = new JTextArea();
		ucast_log.setLineWrap(true);
		ucast_log.setEditable(false);
		ucast_log_pane = new JScrollPane(ucast_log);
		ucast_log_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ucast_log_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ucast_log_pane.setPreferredSize(new Dimension(315, 242));
		
		layout_rules.gridx = 0;
		layout_rules.gridy = 0;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 2;
		layout_rules.insets = new Insets(5, 10, 5, 5);
		add(ucast_log, layout_rules);

		ucast_recipient = new JList();
		DefaultListModel list_contact_jlist_model = new DefaultListModel();
		{
			int taille = contacts.size();
			StringTokenizer st;
			for (int i=0; i<taille; i++)
			{
				st = new StringTokenizer(contacts.get(i), ";");
				st.nextToken();
				
				list_contact_jlist_model.addElement(st.nextToken());
			}
			ucast_recipient.setModel(list_contact_jlist_model);
		}
		ucast_recipient_pane = new JScrollPane(ucast_recipient);
		ucast_recipient_pane.setPreferredSize(new Dimension(100, 237));
		layout_rules.gridx = 1;
		layout_rules.gridy = 0;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(ucast_recipient_pane, layout_rules);
		
		ucast_send_file = new JButton("Fichier");
		ucast_send_file.setPreferredSize(new Dimension(100, 25));
		ucast_send_file.setFocusPainted(false);
		ucast_send_file.addActionListener(this);
		layout_rules.gridx = 1;
		layout_rules.gridy = 1;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(ucast_send_file, layout_rules);

		ucast_chat = new JTextArea();
		ucast_chat.setLineWrap(true);
		ucast_chat_pane = new JScrollPane(ucast_chat);
		ucast_chat_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ucast_chat_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ucast_chat_pane.setPreferredSize(new Dimension(315, 100));
		layout_rules.gridx = 0;
		layout_rules.gridy = 2;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 10, 5, 5);
		add(ucast_chat_pane, layout_rules);
		
		ucast_send_message = new JButton("Envoyer");
		ucast_send_message.setPreferredSize(new Dimension(100, 100));
		ucast_send_message.setFocusPainted(false);
		ucast_send_message.addActionListener(this);
		layout_rules.gridx = 1;
		layout_rules.gridy = 2;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(ucast_send_message, layout_rules);
		
	}
	
	public ArrayList<String> getContacts()
	{
		return contacts;
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getActionCommand().equals("Envoyer"))
		{
			if (ucast_chat.getText().length() > 0)
			{
				String my_contact = Cast.getAddress() + ";" + program.getNickname();
				String[] recipients = new String[contacts.size()];
				contacts.toArray(recipients);
				
				Message message = new Message(my_contact, recipients, 20, ucast_chat.getText());

				program.getCast().sendUnicast(message);
				
				ucast_chat.setText("");
			}
		}
		else
		{
			file_chooser = new JFileChooser();
			file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = file_chooser.showOpenDialog(null);
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = file_chooser.getSelectedFile();
				
				String my_contact = Cast.getAddress() + ";" + program.getNickname();
				String[] recipients = new String[contacts.size()];
				contacts.toArray(recipients);
				
				Message message = new Message(my_contact, recipients, 22, file.getName());
				program.getCast().sendUnicast(message);
				
				message = new Message(my_contact, recipients, 21, file);
				program.getCast().sendUnicast(message);
			}
		}
	}
	
	public void addMessage (Message message)
	{
		String bcast_log_text = ucast_log.getText() ;
		String new_text = Message.getNickname(message.getSender()) + " : " + message.getMessage() + "\n";
		ucast_log.setText(bcast_log_text + new_text);
	}

	public String getFileName ()
	{
		return file_name;
	}
	
	public void setFileName (String name)
	{
		file_name = name;
	}
}
