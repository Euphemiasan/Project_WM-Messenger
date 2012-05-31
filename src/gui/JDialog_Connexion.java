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

// Classe JDialog_Connexion qui correspond au popup de connexion a l'execution du program
public class JDialog_Connexion extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	// Lien avec le programme principal
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
		
		// Met le popup au milieu de la JFrame
		setLocationRelativeTo(null);
		
		// On interdit le redimensionnement
		setResizable(false);
		
		// On interdit a l'utilisateur de fermer le popup, une fois lance, on
		// doit se connecter ou kill le proc
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Mise en place d'un layout null pour faciliter le positionnement du contenu
		setLayout(null);
		
		initComponent();

		// On affiche le pop apres avoir installe les composants
		setVisible(true);
	}

	///////////////////
	// InitComponent //
	///////////////////
	public void initComponent ()
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
		// Message error en rouge
		label_error.setForeground(Color.red);
		label_error.setBounds(27, 50, 140, 25);
		add(label_error);
		
		button_login = new JButton("Login");
		button_login.setBounds(27, 75, 140, 25);
		button_login.addActionListener(this);
		add(button_login);
	}

	////////////////////////////
	// ActionListener Methods //
	////////////////////////////
	public void actionPerformed (ActionEvent e)
	{
		// On test si le pseudo fait un minimum  3 caractere et s'il est disponible via
		// program.availableNickname
		if (nickname.getText().length() > 2 && program.availableNickname(nickname.getText()))
		{
			program.setNickname(nickname.getText());

			// On envoi un signal pour que tout le monde nous rajoute dans la liste de contact
			String my_contact = Cast.getAddress() + ";" + program.getNickname();
			Message message = new Message(my_contact, null, 2, my_contact);
			program.getCast().sendBroadcast(message);
			
			// On met a jour le titre du programme
			program.changeTitle(Cast.getAddress(), program.getNickname());
			
			// On supprime le popup
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
