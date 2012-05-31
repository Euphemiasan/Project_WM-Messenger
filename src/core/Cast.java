package core;



import gui.JDialog_Get_File;
import gui.Panel_Conversation;
import gui.Project_WMMessenger;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Minimal receiver: just prints any message it receives
 * @author t.perennou
 */
public class Cast implements NetListener
{
	private Project_WMMessenger program;
	private static NetInterface netif;

	private JDialog_Get_File dialog_file;

	/*
	 * Differents etats des switch :
	 * 		 0 = message vide qui n'est pas traite
	 * 
	 * 		 1 = message qui demande tous les pseudo
	 * 		-1 = message qui renvoi son pseudo
	 * 		 2 = message equivalent à hello.connect
	 * 		-2 = message equivalent à roger.connect
	 * 		 3 = message equivalent a goodbye.connect
	 * 
	 * 		10 = message texte broadcast
	 * 		11 = message fichier broadcast
	 * 		12 = message nom fichier broadcast
	 * 		13 = message action effectue a la reception du fichier pour fichier envoye en broadcast
	 * 		
	 * 		20 = message texte unicast
	 * 		21 = message fichier unicast
	 * 		22 = message nom fichier unicast
	 * 		23 = message action effectue a la reception du fichier pour fichier envoye en unicast
	 */	
	
	/////////////////
	// Constructor //
	/////////////////
	public Cast(Project_WMMessenger pwmm)
	{
		try
		{
			program = pwmm;
			
			NetInterface.setVerbose(false);
			netif = new NetInterface();
			netif.addNetListener(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/////////////////
	// Send Method //
	/////////////////
	
	// Envoi a 1 personne
	public void sendUnicast (Message message)
	{
		try
		{
			// On test le type de message que l'on envoi
			switch (message.getType())
			{
				// Etat 20 : On envoi un message en unicast
				case 20 :
				{
					// On creer une copie pour nous meme avant d'envoyer le message
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getSelectedComponent();
					conversation.addMessage(message);
					break;
				}
				
				// Etat 21 : On envoi un fichier en unicast
				case 21 :
				{
					String file_name = "";
					try 
					{
						File file = (File) message.getMessage();
						file_name = file.getName();
						FileInputStream fis = new FileInputStream(file);
						byte[] file_byte = new byte[(int) file.length()];
						fis.read(file_byte);
						
						Message new_message = new Message(message.getSender(), message.getRecipients(), message.getType(), file_byte);
						message = new_message;
					}
					catch (FileNotFoundException fnfe)
					{
						fnfe.printStackTrace();
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
					
					// On creer un message pour nous meme disant qu'on est en attente d'un accuse de reception
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getSelectedComponent();
					Message receipt_message = new Message(message.getSender(), null, 0, "envoi en cours " + file_name);
					conversation.addMessage(receipt_message);
					break;
				}
			}
			
			// On envois ensuite le message a tous les destinataires 
			// Marche pour 1 personne et est deja mis en place pour le multicast meme s'il n'a pas pu
			// etre implemente
			for (String recipient : message.getRecipients())
			{
				String adress_recipient = Message.getAddress(recipient);
				netif.sendUnicast(message, new Address(adress_recipient));
			}
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	// Envoi de message en Broadcast
	public void sendBroadcast (Message message)
	{
		try
		{
			switch (message.getType())
			{
				// Etat 10 : On envoi un message en broadcast
				case 10 :
				{
					// On cree une copie pour nous meme
					program.getChat().getBroadcast().addMessage(message);
					break;
				}
				
				// Etat 11 : On envoi un fichier en broadcast
				case 11 :
				{
					String file_name = "";
					try 
					{
						File file = (File) message.getMessage();
						file_name = file.getName();
						FileInputStream fis = new FileInputStream(file);
						byte[] file_byte = new byte[(int) file.length()];
						fis.read(file_byte);
						
						Message new_message = new Message(message.getSender(), message.getRecipients(), message.getType(), file_byte);
						message = new_message;
						
						// Probleme envoi de paquet par broadcast alors on passe par plusieurs 
						// unicast
						for (String contact : program.getListContact().getListContact())
						{
							netif.sendUnicast(message, new Address(Message.getAddress(contact)));
						}
						
						// On creer un message vide avec un getType 0 pour qu'il ne soit pas 
						// traite car on envois un message a la sortie du switch
						message = new Message(message.getSender(), null, 0, null);
					}
					catch (FileNotFoundException fnfe)
					{
						fnfe.printStackTrace();
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
					
					// On creer un message pour nous meme disant qu'on est en attente d'un accuse de reception
					Message receipt_message = new Message(message.getSender(), null, -1, "envoi en cours " + file_name);
					program.getChat().getBroadcast().addMessage(receipt_message);
					break;
				}
			}
			
			netif.sendBroadcast(message);
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	///////////////////
	// Receiv Method //
	///////////////////

	// Reception de message en Unicast
	public void unicastReceived(Address senderAddress, Serializable content)
	{ 
		Message message = (Message) content;
	
		if (senderAddress.toString().equals(getAddress()))
		{	
			// Si on est l'expediteur et le destinataire (ie lors d'un envoi en broadcast)
			// alors on ne traite pas le message
		}
		else
		{
			switch (message.getType())
			{
				// Etat -1 : On recoit un message avec le pseudo de l'expediteur
				case -1 :
				{
					// => Project_WMMessenger.java ligne 104
					// On ajoute le nickname a la liste pour ensuite tester 
					// si notre pseudo est disponible ou non
					program.addNickname((String)message.getMessage());
					break;
				}
				
				// Etat -2 : On recoit un message avec l'adresse (complete) de l'expediteur
				case -2 :
				{
					// On ajoute la personne a la liste de contact
					program.getListContact().addContact((String)message.getMessage());
					break;
				}
				
				// Etat 11 : On recoit le fichier en broacast
				case 11 :
				{
					String file_name = program.getChat().getBroadcast().getFileName();
					
					// On creer un popup avec 3 choix possible
					dialog_file = new JDialog_Get_File(program, "Reception Fichier", true, message, file_name);
					
					String file_path = "";
					
					// On test action de l'utilisateur
					switch (dialog_file.getAction())
					{
						// Cas 1 : On a clique sur Accepter
						case 1 :	
						{
							// On place donc notre fichier dans le repertoire par defaut (ici D:\)
							file_path = "D:\\" + file_name;
							break;
						}
						
						// Cas 2 : On a clique sur Enregistrer Sous
						case 2 :
						{
							// On recupere l'emplacement du fichier
							file_path = dialog_file.getPath();
							break;
						}	
						// Cas 3 : On a clique sur Refuser
						// On ne gere pas ce cas pour l'attribution du path
					}
					
					// Si on a Accepter le fichier (cas 1 ou cas 2), on enregistre le fichier
					if (dialog_file.getAction() != 3)
					{
						try
						{
							byte[] file_byte = (byte[]) message.getMessage();
		
							FileOutputStream fos = new FileOutputStream(file_path);
							fos.write(file_byte);
							fos.close();
						}
						catch (FileNotFoundException fnfe)
						{
							fnfe.printStackTrace();
						}
						catch (IOException ioe)
						{
							ioe.printStackTrace();
						}
						
						// On creer un message pour nous meme disant que l'on a accepte le fichier
						Message receipt_message = new Message(message.getSender(), null, 0, "fichier " + file_name + " reçu");
						program.getChat().getBroadcast().addMessage(receipt_message);
					}
					// Si on a Refuser le fichier
					else
					{
						// On creer un message pour nous meme disant que l'on a refuse le fichier
						Message receipt_message = new Message(message.getSender(), null, 0, "fichier " + file_name + " refusé");
						program.getChat().getBroadcast().addMessage(receipt_message);
					}
					
					// On envoi ensuite un accuse de reception avec l'action effectue concernant le fichier
					String my_contact =  netif.getAddress().toString() + ";" + program.getNickname();
					String[] recipient = {message.getSender()};
					Message acknowledge_receipt_message = new Message(my_contact, recipient, 13, dialog_file.getAction());
					sendUnicast(acknowledge_receipt_message);
					break;
				}
				
				// Etat 13 : On recoit l'accuse de reception du fichier en unicast
				case 13 :
				{
					String action_message = "";
					
					// Selon le choix du destinataire du fichier, on affiche un message different
					if (((Integer) message.getMessage()).intValue() != 3)
					{
						action_message = "fichier accepté";
					}
					else 
					{
						action_message = "fichier refusé";
					}
					
					// On creer un message pour nous meme avec l'action du destinataire du fichier 
					Message action_mes = new Message(message.getSender(), null, -1, action_message);
					program.getChat().getBroadcast().addMessage(action_mes);
					break;
				}
				
				// Etat 20 : On recoit un message texte en unicast
				case 20 :
				{				
					// On recherche dans quelle conversation le message va etre afficher
					int index_tab = getCurrentConversation(message);
					
					// On met une couleur rouge a l'onglet pour mettre en avant les nouveaux messages
					program.getChat().setForegroundAt(index_tab, Color.red);
					
					// On ajoute le message recu a l'historique de conversation
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					conversation.addMessage(message);
					break;
				}
				
				// Etat 21 : On recoit le fichier en unicast
				case 21 : 
				{
					// Meme fonctionnement que la reception de fichier en broadcast, on
					// recupere juste en plus la conversation dans laquelle on insere les messages
					int index_tab = getCurrentConversation(message);
					
					program.getChat().setForegroundAt(index_tab, Color.red);
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					
					dialog_file = new JDialog_Get_File(program, "Reception Fichier", true, message, conversation.getFileName());
					
					String file_path = "";
					switch (dialog_file.getAction())
					{
						case 1 :	
						{
							file_path = "D:\\" + conversation.getFileName();
							break;
						}
						case 2 :
							file_path = dialog_file.getPath();
							break;
					}
					
					if (dialog_file.getAction() != 3)
					{
						try {
							byte[] file_byte = (byte[]) message.getMessage();
		
							FileOutputStream fos = new FileOutputStream(file_path);
							fos.write(file_byte);
							fos.close();
						}
						catch (FileNotFoundException fnfe)
						{
							fnfe.printStackTrace();
						}
						catch (IOException ioe)
						{
							ioe.printStackTrace();
						}
						
						Message receipt_message = new Message(message.getSender(), null, 0, "fichier " + conversation.getFileName() + " reçu");
						conversation.addMessage(receipt_message);
					}
					else
					{
						Message receipt_message = new Message(message.getSender(), null, 0, "fichier " + conversation.getFileName() + " refusé");
						conversation.addMessage(receipt_message);
					}

					String my_contact =  netif.getAddress().toString() + ";" + program.getNickname();
					
					String[] recipient = new String[message.getRecipients().length];
					int i = 0;
					
					for (String contact : message.getRecipients())
					{
						if (!contact.equals(my_contact))
						{
							recipient[i] = contact;
							i++;
						}
					}
					recipient[i] = message.getSender();
					
					Message acknowledge_receipt_message = new Message(my_contact, recipient, 23, dialog_file.getAction());
					sendUnicast(acknowledge_receipt_message);
					
					break;
				}
				
				// Etat 22 : On recoit le nom du fichier qui va etre envoye en unicast
				case 22 :
				{
					int index_tab = getCurrentConversation(message);
					
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					// On l'enregistre, ca permet d'avoir le nom du fichier dans le 
					// JFileChooser avec un nom de fichier par defaut
					conversation.setFileName((String) message.getMessage());
					break;
				}
				
				// Etat 23 : On recoit l'accuse de reception
				// Meme chose que l'accuse de reception en broadcast avec encore une fois
				// la recherche pour trouver le bon onglet
				case 23 : 
				{
					int index_tab = getCurrentConversation(message);

					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					
					String action_message = "";
					if (((Integer) message.getMessage()).intValue() != 3)
					{
						action_message = "fichier accepté";
					}
					else 
					{
						action_message = "fichier refusé";
					}
					
					Message action_mes = new Message(message.getSender(), null, -1, action_message);
					
					conversation.addMessage(action_mes);
					break;
				}
			}
		}
	}

	// Reception de message en Broadcast
	public void broadcastReceived (Address senderAddress, Serializable content) 
	{
		Message message = (Message) content;

		if (senderAddress.toString().equals(getAddress()))
		{
			// Si on est l'expediteur et le destinataire (ie lors d'un envoi en broadcast)
			// alors on ne traite pas le message
		}
		else
		{
			switch (message.getType())
			{
				// Etat 1 : On recoit un message voulant recuperer notre pseudo
				case 1 :
				{
					String my_adress = netif.getAddress().toString();
					String[] recipient = {senderAddress.toString()};
					Message answer = new Message(my_adress, recipient, -1, program.getNickname());
					
					// On envoi notre pseudo
					sendUnicast(answer);
					
					break;
				}
				
				// Etat 2 : On recoit un message hello.connect
				case 2 :
				{
					// On ajoute la personne dans notre liste de contact
					program.getListContact().addContact((String)message.getMessage());
					
					String my_adress = netif.getAddress().toString();
					String[] recipient = {senderAddress.toString()};
					String my_contact = my_adress + ";" + program.getNickname();
					Message answer = new Message(my_adress, recipient, -2, my_contact);

					// Et on lui envoi un roger.connect pour qu'elle nous ajoute dans sa propre 
					// liste
					sendUnicast(answer);
					break;
				}
				
				// Etat 3 : On recoit un message goodbye.connect
				case 3 :
				{
					// On supprme cette personne de notre liste de contact
					program.getListContact().removeContact(message.getSender());
					break;
				}
				
				// Etat 10 : On recoit un message texte en brodcast
				case 10 :
				{
					// On met une alerte pour demarquer les nouveaux messages
					program.getChat().setForegroundAt(0, Color.red);
					// Et on ajoute le message au broadcast
					program.getChat().getBroadcast().addMessage(message);
					break;
				}
				
				// Etat 12 : On recoit le nom du fichier qui va etre envoye
				case 12 :
				{
					// On l'enregistre, ca permet d'avoir le nom du fichier dans le 
					// JFileChooser avec un nom de fichier par defaut
					program.getChat().getBroadcast().setFileName((String) message.getMessage());
					break;
				}
			}
		}
	}

	/////////////
	// Getters //
	/////////////
	public static String getAddress ()
	{
		return netif.getAddress().toString();
	}

	// retrouver l'onglet courant
	public int getCurrentConversation (Message message)
	{
		/*
		 * Un message est compose d'un expediteur et d'un tableau de destinataire. Pour trouver
		 * la conversation courante (si elle existe) trouver une conversation dans laquelle
		 * on est pas parmi les "contacts" (Panel_Conversation attribut ArrayList<String> contact)
		 * et on doit avoir l'expediteur du message ainsi que les autres contacts
		 */
		String my_contact = netif.getAddress().toString() + ";" + program.getNickname();
		String my_sender = message.getSender();
		
		// On transforme le tableau des destinataires en ArrayList
		ArrayList<String> my_recipients = new ArrayList<String>(Arrays.asList(message.getRecipients()));
		
		// On s'enleve des destinataires
		my_recipients.remove(my_contact);

		// On ajoute l'expediteur qui devient pour nous un destinataire
		my_recipients.add(my_sender);
		
		// Et on cherche l'onglet adequat
		int index_tab = program.getChat().findConversation(my_recipients);	
		
		// S'il n'y a pas de conversation ouverte avec les destinaires du message, on 
		// cree un nouvel onglet de discution
		if (index_tab == 0)
		{
			Panel_Conversation conversation;
			conversation = new Panel_Conversation(program, my_recipients);
			program.getChat().addConversation(conversation);
			index_tab = program.getChat().getTabCount() - 1;
		}
		
		// Et on retourne l'index de l'onget
		return index_tab;
	}
}

