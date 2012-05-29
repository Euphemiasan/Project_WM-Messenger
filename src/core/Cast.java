package core;



import gui.Project_WMMessenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Minimal receiver: just prints any message it receives
 * @author t.perennou
 */
public class Cast implements NetListener
{
	private Project_WMMessenger program;
	private static NetInterface netif;

	private String nom_fichier;

	/*
	 * Differents etats des switch :
	 * 		 1 = message pour récupérer tous les pseudo
	 * 		-1 = message qui renvoi son pseudo
	 * 		 2 = message équivalent à hello.connect
	 * 		-2 = message équivalent à roger.connect
	 */
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
	
	public void sendBroadcast (Message message)
	{
		try
		{
			netif.sendBroadcast(message);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public void broadcastReceived (Address senderAddress, Serializable content) 
	{
		Message message = (Message) content;

		if (senderAddress.toString().equals(getAddress()))
		{	
			// Si sender = recipient
		}
		else
		{
			switch (message.getType())
			{
				// Etat 1 : on a recu un message qui veut recuperer les pseudo de tout le monde
				case 1 :
				{
					String my_adress = netif.getAddress().toString();
					String[] recipient = {senderAddress.toString()};
					Message answer = new Message(my_adress, recipient, -1, program.getNickname());
					
					sendUnicast(answer);
					
					break;
				}
				// Etat 2 : on a recu un message qui veut recuperer les adresses de tout le monde
				//			on l'enregistre et on lui renvoi notre adresse
				case 2 :
				{
					program.getListContact().addContact((String)message.getMessage());
					
					String my_adress = netif.getAddress().toString();
					String[] recipient = {senderAddress.toString()};
					String my_contact = my_adress + ";" + program.getNickname();
					Message answer = new Message(my_adress, recipient, -2, my_contact);
	
					sendUnicast(answer);
					
					break;
				}
			}
		}
		/*
		// On actualise la liste de contacts si on recoit "hello.connect" et on renvoit "roger.connect" pour que l'autre utilisateur
		// actualise sa liste de contacts
		if(((String) content).length() > 13 && ((String) content).substring(0, 13).equals("hello.connect"))
		{
			System.out.println("Roger B�b�");
			try {
				netif.sendUnicast("roger.connect",senderAddress);

				if (!senderAddress.toString().equals(getAddress())){

					liste_contact.setlist_contact(senderAddress.toString());
				} 

			} catch (IOException e) {
				e.printStackTrace();
			}	

		}
		
		else if (((String) content).matches("goodbye.connect"))
		{
			String expediteur = senderAddress.toString();
			liste_contact.remove_list_contact(expediteur);
		}
		
		else
		
		{
			String content_1 = broadcast.getbcast_window();
			broadcast.setbcast_window(content_1 + "\n" +senderAddress.toString()  + " : " +content);
		}
		*/
	}
	
	public void sendUnicast (Message message)
	{
		try
		{
			for (String recipient : message.getRecipients())
			{
				netif.sendUnicast(message, new Address(recipient));
			}
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public void unicastReceived(Address senderAddress, Serializable content)
	{
		Message message = (Message) content;
		
		if (senderAddress.toString().equals(getAddress()))
		{	
			// Si sender = recipient
		}
		else
		{
			switch (message.getType())
			{
				// Etat -1 : On recoit un message avec le pseudo de l'expediteur
				case -1 :
				{
					program.addNickname((String)message.getMessage());
					break;
				}
				// Etat -2 : On recoit un message avec l'adresse de l'expediteur
				case -2 :
				{
					program.getListContact().addContact((String)message.getMessage());
					break;
				}
			}
		}
		/*
		if (content instanceof String)
		{
			//Si on recoit "roger.connect" on actualise la liste des contacts
			if(((String) content).matches("roger.connect"))
			{
				liste_contact.setlist_contact(senderAddress.toString());
			}
			else if (((String) content).length() > 9 && ((String) content).substring(0, 9).equals("file.name"))
			{
				nom_fichier = ((String) content).substring(10, ((String) content).length());
			}
			else
			{
				Conversation conv = messenger.trouverConversation(senderAddress.toString());
				String content_1=conv.getucast_window();
				conv.setucast_window(content_1+"\n"+senderAddress.toString()  + " : " + content);
			}
		}
		else
		{
			byte[] fichier_byte = (byte[]) content;
			
			String fichier_path = "D://" + nom_fichier;

			try {
				FileOutputStream fils = new FileOutputStream(fichier_path);
				fils.write(fichier_byte);
				fils.close();
				Conversation conv = messenger.trouverConversation(senderAddress.toString());
				String content_1=conv.getucast_window();
				conv.setucast_window(content_1+"\n"+senderAddress.toString()  + " : Fichier " + nom_fichier + " re�u");
			}
			catch (FileNotFoundException fnfe)
			{
				System.out.println("Cr�er le fichier avant noob");
				fnfe.printStackTrace();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		*/
	}

	public static String getAddress()
	{
		return netif.getAddress().toString();
	}

	public void sendmessage(String destinataire) {

		// envoi du message	
		Conversation conv = messenger.trouverConversation(destinataire);
		Address addr1 = new Address(conv.getucast_address_window());
		try {
			netif.sendUnicast(conv.getucast_chat_field(), addr1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// copie � l'envoyeur
		Address addr2 = netif.getAddress();
		Conversation conv2 = messenger.trouverConversation(addr1.toString());
		conv2.setucast_window(conv.getucast_window()+"\n"+addr2.toString()  + " : " + conv.getucast_chat_field());
	}

	
	
	public void broadcast(String message) {
		
		//envoie du broadcast
		try {
			netif.sendBroadcast(message);
			broadcast.setbcast_chat_field(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	

	//M�thode appel�e � la connexion
	public void hello()
	{
		try {
			System.out.println("hello.connect." + messenger.getpseudo());
			netif.sendBroadcast("hello.connect." + messenger.getpseudo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	//M�thode appel�e � la d�connexion
	public void goodbye()
	{
		try {
			netif.sendBroadcast("goodbye.connect");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Envoi de fichiers
	public void sendFile_Unicast(File fichier, String destinataire)
	{
		try 
		{
			FileInputStream fils;
			fils = new FileInputStream(fichier);
			byte[] fichier_byte = new byte[(int) fichier.length()];
			fils.read(fichier_byte);
			
			Conversation conv = messenger.trouverConversation(destinataire);
			Address addr1 = new Address(destinataire);
			netif.sendUnicast(fichier_byte, addr1);

			Address addr2 = netif.getAddress();
			Conversation conv2 = messenger.trouverConversation(addr1.toString());
			conv2.setucast_window(conv.getucast_window()+"\n"+addr2.toString()  + " : Fichier envoy�");
		} 
		catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

}

