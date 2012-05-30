package core;



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

import javax.swing.JFileChooser;

/**
 * Minimal receiver: just prints any message it receives
 * @author t.perennou
 */
public class Cast implements NetListener
{
	private Project_WMMessenger program;
	private static NetInterface netif;

	/*
	 * Differents etats des switch :
	 * 		 1 = message qui demande tous les pseudo
	 * 		-1 = message qui renvoi son pseudo
	 * 		 2 = message equivalent à hello.connect
	 * 		-2 = message equivalent à roger.connect
	 * 		 3 = message equivalent a goodbye.connect
	 * 
	 * 		10 = message texte broadcast
	 * 		11 = message fichier broadcast
	 * 		12 = message nom fichier unicast
	 * 		
	 * 		20 = message texte unicast
	 * 		21 = message fichier unicast
	 * 		22 = message nom fichier unicast
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
	
	public void sendUnicast (Message message)
	{
		try
		{
			switch (message.getType())
			{
				// Etat 20 : On envoi un message en unicast
				case 20 :
				{
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
					
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getSelectedComponent();
					Message receipt_message = new Message(message.getSender(), null, -1, "fichier " + file_name +" envoyé");
					conversation.addMessage(receipt_message);
					break;
				}
			}
			
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
	
	public void sendBroadcast (Message message)
	{
		try
		{
			netif.sendBroadcast(message);
			
			switch (message.getType())
			{
				// Etat 10 : On envoi un message en broadcast
				case 10 :
				{
					program.getChat().getBroadcast().addMessage(message);
					break;
				}
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
				// Etat 20 : On recoit un message texte en unicast
				case 20 :
				{
					int index_tab = getCurrentConversation(message);
					
					program.getChat().setForegroundAt(index_tab, Color.red);
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					conversation.addMessage(message);
					break;
				}
				// Etat 21 : On recoit le fichier
				case 21 : 
				{
					int index_tab = getCurrentConversation(message);
					
					program.getChat().setForegroundAt(index_tab, Color.red);
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					try {
						byte[] file_byte = (byte[]) message.getMessage();
						
						String file_path = "D://" + conversation.getFileName();
	
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
					break;
				}
				// Etat 22 : On recoit le nom du fichier qui va etre envoye
				case 22 :
				{
					int index_tab = getCurrentConversation(message);
					
					program.getChat().setForegroundAt(index_tab, Color.red);
					Panel_Conversation conversation = (Panel_Conversation) program.getChat().getComponentAt(index_tab);
					conversation.setFileName((String) message.getMessage());
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
				// Etat 1 : On recoit un hello
				case 1 :
				{
					String my_adress = netif.getAddress().toString();
					String[] recipient = {senderAddress.toString()};
					Message answer = new Message(my_adress, recipient, -1, program.getNickname());
					
					sendUnicast(answer);
					
					break;
				}
				// Etat 2 : On recoit un message roger
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
				// Etat 3 : On recoit un message goodbye
				case 3 :
				{
					program.getListContact().removeContact(message.getSender());
					break;
				}
				// Etat 10 : On recoit un message texte en broadcast
				case 10 :
				{
					program.getChat().getBroadcast().addMessage(message);
					program.getChat().setForegroundAt(0, Color.red);
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

	public static String getAddress ()
	{
		return netif.getAddress().toString();
	}

	public int getCurrentConversation (Message message)
	{
		String my_contact = netif.getAddress().toString() + ";" + program.getNickname();
		String my_sender = message.getSender();
		
		ArrayList<String> my_recipients = new ArrayList<String>(Arrays.asList(message.getRecipients()));
		my_recipients.remove(my_contact);
		my_recipients.add(my_sender);
		
		int index_tab = program.getChat().findConversation(my_recipients);
		
		Panel_Conversation conversation;
		if (index_tab == 0)
		{
			conversation = new Panel_Conversation(program, my_recipients);
			program.getChat().addConversation(conversation);
			index_tab = program.getChat().getTabCount() - 1;
		}
		
		return index_tab;
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
			
			Conversation conv = messenger.findConversation(destinataire);
			Address addr1 = new Address(destinataire);
			netif.sendUnicast(fichier_byte, addr1);

			Address addr2 = netif.getAddress();
			Conversation conv2 = messenger.findConversation(addr1.toString());
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

