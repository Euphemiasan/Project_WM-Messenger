package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import core.Cast;
import core.Message;

public class JDialog_Connexion extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private Project_WMMessenger program;
	
	private JLabel label_nickname;
	private JTextField nickname;
	private JLabel label_error;
	private JButton button_login;
	
	public JDialog_Connexion (JFrame parent, String title, boolean modal) 
	{
		super(parent, title, modal);
		
		program = (Project_WMMessenger) parent;
		setPreferredSize(new Dimension(200, 135));
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
		label_nickname = new JLabel("Nickname : ");
		label_nickname.setHorizontalAlignment(JLabel.CENTER);
		label_nickname.setBounds(2, 0, 190, 25);
		add(label_nickname);
		
		nickname = new JTextField();
		nickname.setBounds(27, 25, 140, 20);
		add(nickname);
		
		label_error = new JLabel("");
		label_error.setHorizontalAlignment(JLabel.CENTER);
		label_error.setVerticalAlignment(JLabel.TOP);
		label_error.setForeground(Color.red);
		label_error.setBounds(27, 50, 140, 25);
		add(label_error);
		
		button_login = new JButton("Login");
		button_login.setBounds(27, 75, 140, 25);
		button_login.addActionListener(this);
		add(button_login);
	}

	public void actionPerformed (ActionEvent e)
	{
		if (nickname.getText().length() > 2 && program.availableNickname(nickname.getText()))
		{
			program.setNickname(nickname.getText());
			
			String my_adress = Cast.getAddress();
			String my_nickname = program.getNickname();
			String my_contact = my_adress + ";" + my_nickname;
			Message message = new Message(my_adress, null, 2, my_contact);
			program.getCast().sendBroadcast(message);
			
			program.changeTitle(my_adress, my_nickname);
			
			setVisible(false);
		}
		else if (nickname.getText().length() < 3)
		{
			label_error.setText("Nickname trop court");
		}
		else
			label_error.setText("Nickname déjà utilisé");
	}
	

}
