package gui;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Panel_Conversation extends JPanel
{
	private JScrollPane ucast_log_pane;
	private JTextArea ucast_log;
	private JScrollPane ucast_chat_pane;
	private JTextArea ucast_chat;
	private JButton ucast_send_message;
	private JButton ucast_send_file;
	private JScrollPane ucast_recipient_pane;
	private JList ucast_recipient;
	
	public Panel_Conversation ()
	{
		super();
	}
}
