package core;


import java.io.Serializable;

/**
 * Allow the reception of messages sent in unicast or broadcast mode.
 * @author t.perennou
 */
public interface NetListener {
	
	/**
	 * Called when a unicast message is received
	 * @param senderAddress Sender's address
	 * @param content Message content
	 */
	void unicastReceived(Address senderAddress, Serializable content);
	
	/**
	 * Called when a broadcast message is received
	 * @param senderAddress Sender's address
	 * @param content Message content
	 */
	void broadcastReceived(Address senderAddress, Serializable content);

}



