package core;

import java.io.Serializable;
import java.util.StringTokenizer;

// Classe Message qui correspond a TOUS les messages envoye sur le reseau
public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// Adresse et Pseudo de l'expediteur sous la forme : "IP:Port;Pseudo"
	private String sender;
	
	// Tableau des differents destinataires
	private String[] recipients;
	
	// Type du message 
	private int message_type;
	
	// Contenu du message : texte, id, fichier
	private Serializable message_content;
	
	public Message (String s, String[] r, int t, Serializable c)
	{
		sender = s;
		recipients = r;
		message_type = t;
		message_content = c;
	}
	
	/////////////
	// Getters //
	/////////////
	public String getSender ()
	{
		return sender;
	}

	public String[] getRecipients ()
	{
		return recipients;
	}
	
	public int getType ()
	{
		return message_type;
	}
	
	public Serializable getMessage ()
	{
		return message_content;
	}
	
	////////////////////
	// Getter Statics //
	////////////////////
	
	// Recupere l'adresse dans une forme dans une String au meme format que l'attribut sender
	public static String getAddress (String full_adress)
	{
		// String Tokenizer est une sorte d'iterator qui parcours la chaine et s'arrete
		// a chaque delimiteur
		StringTokenizer st = new StringTokenizer(full_adress, ";");
		
		return st.nextToken();
	}

	// Recupere le pseudo dans une forme dans une String au meme format que l'attribut sender
	public static String getNickname (String full_adress)
	{
		StringTokenizer st = new StringTokenizer(full_adress, ";");
		
		// Le premier token correspond a l'adresse, donc on fait un premier nextToken()
		// a blanc et on renvoi le 2e
		st.nextToken();
		
		return st.nextToken();
	}
	
}
