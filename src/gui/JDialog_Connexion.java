package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class JDialog_Connexion extends JDialog implements ActionListener
{
	private Project_WMMessenger program;
	
	private JLabel label_nickname;
	private JTextField nickname;
	private JLabel label_error;
	private JButton button_login;
	
	public JDialog_Connexion (JFrame parent, String title, boolean modal) 
	{
		super(parent, title, modal);
		
		// On lie le programme principal pour recuperer le pseudo
		program = (Project_WMMessenger) parent;

		setPreferredSize(new Dimension(200, 135));
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		// Il ne se passe rien quand on essaie de fermer le JDialog
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		initComponent();

		setVisible(true);
	}
	
	private void initComponent ()
	{
		setLayout(null);
		label_nickname = new JLabel("Nickname : ");
		label_nickname.setHorizontalAlignment(JLabel.CENTER);
		label_nickname.setBounds(2, 0, 190, 25);
		add(label_nickname);
		
		nickname = new JTextField();
		nickname.setBounds(27, 25, 140, 20);
		add(nickname);
		
		label_error = new JLabel();
		label_error.setBounds(27, 50, 140, 25);
		
		button_login = new JButton("Login");
		button_login.setBounds(27, 75, 140, 25);
		button_login.addActionListener(this);
		add(button_login);
		
	}

	public void actionPerformed (ActionEvent e)
	{
		/*
		if (txtpseudo.getText().length() > 2 && pseudoDisponible())
		{
			messenger.setPseudo(txtpseudo.getText());
			setVisible(false);
			messenger.getCast().hello();
		}
		else if (txtpseudo.getText().length() < 3)
			txtpseudo.setText("Pseudo trop court");
		
		else
			txtpseudo.setText("Pseudo indisponible");
		
		*/
	}
	

}
