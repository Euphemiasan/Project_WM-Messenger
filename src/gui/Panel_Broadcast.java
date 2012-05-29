package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Panel_Broadcast extends JPanel
{
	private JScrollPane bcast_log_pane;
	private JTextArea bcast_log;
	private JScrollPane bcast_chat_pane;
	private JTextArea bcast_chat;
	private JButton bcast_send_message;
	private JButton bcast_send_file;
	private JLabel bcast_image;
	
	public Panel_Broadcast ()
	{
		super();
		
		setPreferredSize(new Dimension(483, 426));
		setBackground(new Color(200, 221, 242));
		
		GridBagLayout bcast_layout = new GridBagLayout();
		setLayout(bcast_layout);
		
		GridBagConstraints layout_rules = new GridBagConstraints();
		layout_rules.fill = GridBagConstraints.BOTH;
		layout_rules.weightx = 0;
		layout_rules.weighty = 0;
		layout_rules.anchor = GridBagConstraints.CENTER;

		bcast_log = new JTextArea();
		bcast_log.setEditable(false);
		bcast_log_pane = new JScrollPane(bcast_log);
		bcast_log_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		bcast_log_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		bcast_log_pane.setPreferredSize(new Dimension(315, 242));
		layout_rules.gridx = 0;
		layout_rules.gridy = 0;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 2;
		layout_rules.insets = new Insets(5, 10, 5, 5);
		add(bcast_log_pane, layout_rules);

		bcast_image = new JLabel(new ImageIcon("broadcast.png"));
		bcast_image.setPreferredSize(new Dimension(100, 237));
		layout_rules.gridx = 1;
		layout_rules.gridy = 0;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(bcast_image, layout_rules);
		
		bcast_send_file = new JButton("Fichier");
		bcast_send_file.setPreferredSize(new Dimension(100, 25));
		bcast_send_file.setFocusPainted(false);
		layout_rules.gridx = 1;
		layout_rules.gridy = 1;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(bcast_send_file, layout_rules);

		bcast_chat = new JTextArea();
		bcast_chat_pane = new JScrollPane(bcast_chat);
		bcast_chat_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		bcast_chat_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		bcast_chat_pane.setPreferredSize(new Dimension(315, 100));
		layout_rules.gridx = 0;
		layout_rules.gridy = 2;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 10, 5, 5);
		add(bcast_chat_pane, layout_rules);
		
		bcast_send_message = new JButton("Envoyer");
		bcast_send_message.setPreferredSize(new Dimension(100, 100));
		bcast_send_message.setFocusPainted(false);
		layout_rules.gridx = 1;
		layout_rules.gridy = 2;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(bcast_send_message, layout_rules);
		
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
	}
}
