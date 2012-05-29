package core;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Message implements Serializable
{
	private String sender;
	private String[] recipients;
	private int message_type;
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
	public static String getAddress (String full_adress)
	{
		StringTokenizer st = new StringTokenizer(full_adress, ";");
		
		return st.nextToken();
	}
	
	public static String getNickname (String full_adress)
	{
		StringTokenizer st = new StringTokenizer(full_adress, ";");
		st.nextToken();
		
		return st.nextToken();
	}
	
}
