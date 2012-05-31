package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.Cast;
import core.Message;

// Classe Panel_Broadcast qui correspond au chat pour tout le monde
public class Panel_Broadcast extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	// Lien avec le programme principal
	private Project_WMMessenger program;
	
	// Historique de conversation
	private JScrollPane bcast_log_pane;
	private JTextArea bcast_log;

	// JTextArea envoi de message
	private JScrollPane bcast_chat_pane;
	private JTextArea bcast_chat;
	
	// Boutton envoi de message
	private JButton bcast_send_message;
	
	// Boutton envoi de fichier
	private JButton bcast_send_file;
	
	// Image broadcast
	private JLabel bcast_image;
	
	// Gestion d'envoi de fichier
	private JFileChooser file_chooser;
	private String file_name;

	/////////////////
	// Constructor //
	/////////////////
	public Panel_Broadcast (Project_WMMessenger pwmm)
	{
		super();
		
		program = pwmm;
		
		setPreferredSize(new Dimension(483, 426));
		setBackground(new Color(200, 221, 242));
		
		initComponent();
	}
	
	///////////////////
	// InitComponent //
	///////////////////
	public void initComponent ()
	{
		// Gestionnaire de mise en forme en grille
		GridBagLayout bcast_layout = new GridBagLayout();
		setLayout(bcast_layout);
		
		// Contraintes pour le gestionnaire
		GridBagConstraints layout_rules = new GridBagConstraints();
		layout_rules.fill = GridBagConstraints.BOTH;
		layout_rules.weightx = 0;
		layout_rules.weighty = 0;
		layout_rules.anchor = GridBagConstraints.CENTER;

		bcast_log = new JTextArea();
		// Retour a la line automatique
		bcast_log.setLineWrap(true);
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
		bcast_send_file.addActionListener(this);
		layout_rules.gridx = 1;
		layout_rules.gridy = 1;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(bcast_send_file, layout_rules);

		bcast_chat = new JTextArea();
		bcast_chat.setLineWrap(true);
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
		bcast_send_message.addActionListener(this);
		layout_rules.gridx = 1;
		layout_rules.gridy = 2;
		layout_rules.gridwidth = 1;
		layout_rules.gridheight = 1;
		layout_rules.insets = new Insets(5, 5, 5, 10);
		add(bcast_send_message, layout_rules);
		
	}

	////////////////////////////
	// ActionListener Methods //
	////////////////////////////
	public void actionPerformed (ActionEvent ae)
	{
		if (ae.getActionCommand().equals("Envoyer"))
		{
			// On envoi un message s'il n'est pas vide
			if (bcast_chat.getText().length() > 0)
			{
				String my_contact = Cast.getAddress() + ";" + program.getNickname();
				Message message = new Message(my_contact, null, 10, bcast_chat.getText());
				
				program.getCast().sendBroadcast(message);
				
				// Quand le message est envoye, on remet a zero la ligne de chat
				// et on lui redonne le focus
				bcast_chat.setText("");
				bcast_chat.requestFocusInWindow();

			}
		}
		else
		{
			file_chooser = new JFileChooser();
			file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = file_chooser.showOpenDialog(null);
			
			// Si on a bien recuperer un fichier, on l'envoi
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = file_chooser.getSelectedFile();
				
				String my_contact = Cast.getAddress() + ";" + program.getNickname();
				
				// On envoi d'abord le nom du fichier
				Message message = new Message(my_contact, null, 12, file.getName());
				program.getCast().sendBroadcast(message);
				
				// Puis on envoi le fichier
				message = new Message(my_contact, null, 11, file);
				program.getCast().sendBroadcast(message);
			}
		}
	}

	////////////
	// Getter //
	////////////
	public String getFileName ()
	{
		return file_name;
	}

	////////////
	// Setter //
	////////////
	public void setFileName (String name)
	{
		file_name = name;
	}
	
	//////////////////////
	// Personal Methods //
	//////////////////////
	public void addMessage (Message message)
	{
		String bcast_log_text = bcast_log.getText() ;
		String new_text = Message.getNickname(message.getSender()) + " : " + message.getMessage() + "\n";
		bcast_log.setText(bcast_log_text + new_text);
	}

}
