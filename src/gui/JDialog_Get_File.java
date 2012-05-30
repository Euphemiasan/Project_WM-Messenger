package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import core.Message;

public class JDialog_Get_File extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private Message message;
	private String file_name;

	private JLabel label_message;
	private JLabel label_path;
	private JButton button_accept;
	private JButton button_save_as;
	private JButton button_cancel;

	private JFileChooser file_chooser;
	
	private int action = -1;
	private String file_path;
	
	public JDialog_Get_File (JFrame parent, String title, boolean modal, Message mes, String name)
	{
		super(parent, title, modal);
		
		message = mes;
		file_name = name;
		
		setPreferredSize(new Dimension(400, 200));
		pack();
		
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(null);

		initComponent();

		setVisible(true);
	}

	private void initComponent ()
	{
		label_message = new JLabel(Message.getNickname(message.getSender()) + " souhaite vous envoyer " + file_name);
		label_message.setHorizontalAlignment(JLabel.CENTER);
		label_message.setBounds(2, 0, 390, 50);
		add(label_message);

		label_path = new JLabel("Répertoire par défaut : D:\\");
		label_path.setHorizontalAlignment(JLabel.CENTER);
		label_path.setBounds(2, 50, 390, 50);
		add(label_path);

		button_accept = new JButton("Accepter");
		button_accept.setBounds(15, 110, 90, 30);
		button_accept.addActionListener(this);
		add(button_accept);
		
		button_save_as = new JButton("Enregistrer sous");
		button_save_as.setBounds(120, 110, 160, 30);
		button_save_as.addActionListener(this);
		add(button_save_as);
		
		button_cancel = new JButton("Refuser");
		button_cancel.setBounds(295, 110, 90, 30);
		button_cancel.addActionListener(this);
		add(button_cancel);
		
	}

	public void actionPerformed (ActionEvent ae)
	{
		if (ae.getActionCommand().equals("Accepter"))
		{
			action = 1;
		}
		else if (ae.getActionCommand().equals("Enregistrer sous"))
		{
			action = 2;
			File file = new File(file_name);
			file_chooser = new JFileChooser();
			file_chooser.setSelectedFile(file);
			file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int returnVal = file_chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = file_chooser.getSelectedFile();
				
				file_path = file.getPath();
			}
		}
		else
		{
			action = 3;
		}
		
		setVisible(false);
	}
	
	public int getAction ()
	{
		return action;
	}

	public String getPath ()
	{
		return file_path;
	}
}
