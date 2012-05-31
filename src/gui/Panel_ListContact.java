package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import core.Message;

// CLasse Panel_ListContact qui correspond au panneau a gauche dans le programme
public class Panel_ListContact extends JPanel
{
	private static final long serialVersionUID = 1L;

	// Liste des contacts sous la forme : "IP:PORT;Pseudo"
	private ArrayList<String> list_contact;
	
	// Lien avec le programme principal
	private Project_WMMessenger program;
	
	private JLabel list_contact_title;
	private JScrollPane list_contact_scrollpane;
	private JList list_contact_jlist;

	/////////////////
	// Constructor //
	/////////////////
	public Panel_ListContact (Project_WMMessenger pwmm)
	{
		super();
		
		program = pwmm;
		list_contact = new ArrayList<String>();
		
		setPreferredSize(new Dimension(150, 400));
		setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		
		initComponent();
	}
	
	///////////////////
	// InitComponent //
	///////////////////
	public void initComponent ()
	{
		list_contact_title = new JLabel(" Liste de Contacts");
		list_contact_title.setPreferredSize(new Dimension(150, 20));
		// Le background d'un JLabel est par defaut invisible
		list_contact_title.setOpaque(true);
		// On change ensuite sa couleur
		list_contact_title.setBackground(new Color(50, 150, 200));
		add(list_contact_title);
		
		// Creation d'un autre classe anonyme qui permet de gerer les infobulles
		list_contact_jlist = new JList()
		{
			private static final long serialVersionUID = 1L;

			// Si on passe sa souris sur un pseudo, on affiche son addresse
			public String getToolTipText(MouseEvent me) {
				int index = locationToIndex(me.getPoint());

				if (index > -1) {
					return Message.getAddress(list_contact.get(index));
				}
				
				return null;
			}
		};
		list_contact_jlist.setBackground(new Color(175, 225, 255));
		list_contact_jlist.setSelectionBackground(new Color(135, 206, 235));
		
		// Ajout d'un mouseListener pour ouvrir une nouvelle conversation avec un double click
		list_contact_jlist.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked (MouseEvent me)
			{
				if (me.getClickCount() == 2)
				{
					// On recupere l'adresse (complete) du contact sur qui on a double clique
					int index = list_contact_jlist.locationToIndex(me.getPoint());
					String recipient = list_contact.get(index);
					ArrayList<String> recipients = new ArrayList<String>();
					recipients.add(recipient);
					
					// On regarde si on a deja une conversation ouverte avec le(s) 
					// destinataire(s)
					int index_tab = program.getChat().findConversation(recipients);
					
					// Si ce n'est pas le cas, un cree un onglet avec cette personne
					// et on met a jour index_tab
					if (index_tab == 0)
					{
						Panel_Conversation conversation = new Panel_Conversation(program, recipients);
						program.getChat().addConversation(conversation);
						index_tab = program.getChat().getTabCount() - 1;
					}

					// On met le focus sur la conversation selectionnee
					program.getChat().setSelectedIndex(index_tab);
				}
			}
		});

		list_contact_jlist.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent ke)
			{
				if (ke.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (list_contact_jlist.getSelectedValues().length > 0)
					{
						ArrayList<String> recipients = new ArrayList<String>();
						
						for (int y : list_contact_jlist.getSelectedIndices())
						{
							recipients.add((String) list_contact.get(y));
						}

						int index_tab = program.getChat().findConversation(recipients);
						
						if (index_tab == 0)
						{
							Panel_Conversation conversation = new Panel_Conversation(program, recipients);
							program.getChat().addConversation(conversation);
							index_tab = program.getChat().getTabCount() - 1;
						}

						program.getChat().setSelectedIndex(index_tab);
					}
				}
				
			}
		});

		list_contact_scrollpane = new JScrollPane(list_contact_jlist);
		
		// On supprime la bordure par defaut d'un ScrollPane 
		Border border = BorderFactory.createEmptyBorder();  
		list_contact_scrollpane.setBorder(border);
		
		add(list_contact_scrollpane);

	}
	
	public ArrayList<String> getListContact ()
	{
		return list_contact;
	}

	//////////////////////
	// Personal Methods //
	//////////////////////
	public void addContact (String contact)
	{
		list_contact.add(contact);
		
		// On rafraichit la JListe de contact apres avoir ajoute un contact
		// dans l'ArrayList
		refreshJList();
	}
	
	public void removeContact (String contact)
	{
		// On cree un iterator qui parcours la liste de contact et cherche le contact a supprimer
		ListIterator<String> it = list_contact.listIterator();
		while (it.hasNext())
		{
			String current_contact = it.next();
			
			if (current_contact.equals(contact))
				it.remove();
		}
		
		// Et on rafraichit
		refreshJList();
	}
	
	// Methode qui permet de rafraichir la JList
	public void refreshJList ()
	{
		DefaultListModel list_contact_jlist_model = new DefaultListModel();
		int taille = list_contact.size();
		
		// Pour tous les contacts on recupere le pseudo grace a la methode static
		// de Message
		for (int i=0; i<taille; i++)
			list_contact_jlist_model.addElement(Message.getNickname(list_contact.get(i)));
		
		// On supprime le precedent contenu
		list_contact_jlist.removeAll();

		// On met en place le nouveau contenu
		list_contact_jlist.setModel(list_contact_jlist_model);
	}

	// Methode qui met a jour la taille du composant Panel_ListContact
	public void refreshSize (int height)
	{
		// Le panel est extansible en hauteur mais garde une valeur fixe en largeur
		setPreferredSize(new Dimension(150, height));
		
		// 20 correspond a la hauteur du JLabel;
		list_contact_scrollpane.setPreferredSize(new Dimension(150, height-20));
	}
}