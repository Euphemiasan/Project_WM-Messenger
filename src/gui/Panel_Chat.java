package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Message;

// Classe Panel_Chat correspond a l'ensemble des onglets dans le panneau droite
// du programme
public class Panel_Chat extends JTabbedPane implements ChangeListener
{
	private static final long serialVersionUID = 1L;

	// Lien avec le programme
	private Project_WMMessenger program;
	
	// Panel unique broadcast
	private Panel_Broadcast panel_broadcast;
	
	// Tableau des differentes conversations
	private Panel_Conversation[] conversations;
	
	/////////////////
	// Constructor //
	/////////////////
	public Panel_Chat (Project_WMMessenger pwmm)
	{
		// Les onglet se situeront en haut
		super(JTabbedPane.TOP);

		program = pwmm;
		
		addChangeListener(this);
		
		initComponent();
	}

	///////////////////
	// InitComponent //
	///////////////////
	public void initComponent()
	{
		panel_broadcast = new Panel_Broadcast(program);
		addTab("Broadcast", null, panel_broadcast, null);
		
		conversations = new Panel_Conversation[0];
	}
	
	////////////
	// Getter //
	////////////
	public Panel_Broadcast getBroadcast ()
	{
		return panel_broadcast;
	}

	//////////////////////
	// Personal Methods //
	//////////////////////
	public void addConversation (Panel_Conversation conversation)
	{
		// On creer le nouveau tableau
		Panel_Conversation[] conversations_tmp = new Panel_Conversation[conversations.length+1];
		
		// On transfert les anciennes donnees
		for (int i=0; i<conversations.length; i++)
		{
			conversations_tmp[i] = conversations[i];
		}
		
		// On ajoute la nouvelle conversation
		conversations_tmp[conversations_tmp.length-1] = conversation;
		
		// On pointe la variable vers le nouveau tableau
		conversations = conversations_tmp;
		
		// Si la conversation contient seulement 2 participant on met le pseudo
		// de la personne a qui l'on parle
		if (conversation.getContacts().size() == 1)
		{
			String nickname = Message.getNickname(conversation.getContacts().get(0));
			addTab(nickname, null, conversation, null);
		}
		// Sinon on met un nom general [PAS ENCORE IMPLEMENTEE]
		else
		{
			addTab(conversation.getContacts().size() + " personnes", null, conversation, null);
		}
		
	}
	
	// On cherche si on a un onglet avec les differents contacts, et on retourne un index
	// (-1 correspond a non)
	public int findConversation (ArrayList<String> contacts)
	{
		// index que l'on retourne
		int index = -1;
		
		// variable qui sert d'index pendant qu'on parcourt le tableau
		int i = -1;
		
		// variable qui test chaque conversation
		boolean checked;
		
		for (Panel_Conversation conversation : conversations)
		{
			i++;
			// A chaque nouvelle conversation on remet le compteur a true
			checked = true;

			// On test deja si le nombre de participant correspond, si ce n'est pas
			// le cas on passe directement a la prochaine conversation
			if (conversation.getContacts().size() == contacts.size())
			{
				// Si le nombre de participant correspond on test la correspondance
				// avec tous les participants
				for (String contact : conversation.getContacts())
				{
					// Si la conversation actuelle ne contient pas le contact courant 
					// on passe directement a une nouvelle conversation
					if (!contacts.contains(contact))
					{
						checked = false;
						break;
					}
				}
				// Si la conversation a ete trouvee on sort de la boucle
				if (checked)
				{
					index = i;
					break;
				}
			}
		}
		
		return index + 1;
	}
	
	// Met a jour la taille du composant
	public void refreshSize (int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		revalidate();
	}
	
	////////////////////////////
	// ChangeListener Methods //
	////////////////////////////
	public void stateChanged (ChangeEvent ce)
	{
		// Quand on change d'onglet, on met a jour la couleur du titre de l'onglet
		setForegroundAt(getSelectedIndex(), Color.BLACK);
	}

}
