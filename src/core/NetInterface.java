package core;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class allowing to send and receive messages from a Java application.
 * @author t.perennou
 */
public class NetInterface {

    public static final String BROADCAST_ADDRESS = "255.255.255.255";
    public static final int BROADCAST_PORT = 30000;
    public static final int UNICAST_PORT = 40000;
	public static final int NB_BROADCAST_PORTS = 100;
	public static final int UDP_PACKET_SIZE = 1400;
	public static final int NB_UNICAST_CONNECTIONS = 100;
	public static final long LISTEN_DELAY = 50;
	public static final long CLOSE_DELAY = 0;
	
	private static boolean verbose = false;

	private BroadcastReceiver broadcastReceiver;
	private UnicastReceiver unicastReceiver;
	private List<NetListener> listeners;
	private Map<Address, ObjectOutputStream> unicastStreams;
	private DatagramSocket broadcastSocket;

	/** 
	 * Turn comments on or off for all net.* classes.
	 * @param verbose Use true to turn comments on.
	 */
	public static void setVerbose(boolean verbose) {
		NetInterface.verbose = verbose;
		// do that for net.internal classes too
		BroadcastReceiver.verbose = verbose;
		UnicastReceiver.verbose = verbose;
	}

	/**
	 * Create a new instance of NetInterface. The program is then active
	 * until all connections are closed.
	 * @see #close()
	 */
	public NetInterface() {
		listeners = new ArrayList<NetListener>();
		unicastStreams = new TreeMap<Address, ObjectOutputStream>();
		unicastReceiver = new UnicastReceiver(this, UNICAST_PORT);
		try {
			Thread.sleep(LISTEN_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broadcastReceiver = new BroadcastReceiver(this);
		try {
			Thread.sleep(LISTEN_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// the application address has been set by UnicastReceiver
		if(getAddress().isLoopback()) {
			System.out.println("Net: warning: application cannot send broadcast messages (not connected to a real network)");
		}
	}

	/**
	 * Closes all network connections for reception. The program
	 * can still send messages.
	 */
	public void close() {
		if (verbose) {
			System.out.println(("Net: closing..."));
		}
		broadcastReceiver.close();
		unicastReceiver.close();
		try {
			Thread.sleep(CLOSE_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Address on which the instance is receiving unicast messages
	 */
	public Address getAddress() {
		return unicastReceiver.getAddress();
	}

	/* Unicast sending functions */

	/**
	 * Send a unicast message to the specified destination. The connection must
	 * have been established first.
	 * 
	 * @param content
	 *            Message content (can be a String, etc.)
	 * @param to
	 *            Destination address
	 * @throws IOException
	 */
	public void sendUnicast(Serializable content, Address to) throws IOException {
		ObjectOutputStream out = unicastStreams.get(to);
		// create output stream if necessary
		if (out == null) {
			// create socket
			Socket tcpClientSocket = new Socket(to.getIpAddress(), to.getTcpPort());
			// build a stream to write objects on that socket
			out = new ObjectOutputStream(tcpClientSocket.getOutputStream());
			unicastStreams.put(to, out);
			// send this application's address
			out.writeObject(getAddress());
			out.flush();
			if (verbose) {
				System.out.println("Net: sender connected to "
						+ tcpClientSocket.getInetAddress().getHostAddress() + ":"
						+ tcpClientSocket.getPort());
			}
		}
		out.writeObject(content);
		out.flush();
		if (verbose) {
			System.out.println("Net: sender sent " + content + " to " + to);
		}
	}

	/* Broadcast method */

	/**
	 * Sends a broadcast message to all applications listening on the same local
	 * network. One of the machine network interface (WiFi or Ethernet) must be
	 * ready, or you will get an IO exception "No Route to Host".
	 * 
	 * @param content
	 *            Message content (can be a String, etc.)
	 * @throws IOException
	 */
	public void sendBroadcast(Serializable content) throws IOException {
		// create broadcast if necessary
		if (broadcastSocket == null) {
			broadcastSocket = new DatagramSocket();
			broadcastSocket.setBroadcast(true);
		}

		// transform the object into a byte array
		// the message contains both the object and the address of the sender
		ByteArrayOutputStream baos = new ByteArrayOutputStream(UDP_PACKET_SIZE);
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(getAddress());
		os.writeObject(content);
		byte[] data = baos.toByteArray();
		os.close();

		// send the byte array as a broadcast message
		DatagramPacket packet = new DatagramPacket(data, data.length);
		packet.setAddress(InetAddress.getByName(BROADCAST_ADDRESS));
		// use several ports for the case of several applications running on
		// the same machine; they are then listening on different ports
		int startPort = BROADCAST_PORT;
		int endPort = BROADCAST_PORT + NB_BROADCAST_PORTS - 1;
		for (int port = startPort; port < endPort; port++) {
			packet.setPort(port);
			broadcastSocket.send(packet);
		}
		if (verbose) {
			System.out.println("Net: sender broadcasted " + content + " on ports "
					+ startPort + ".." + endPort);
		}
	}

	/* Reception listeners management functions */

	/**
	 * Add a listener to the list of registered listeners. Registered listeners will
	 * get all broadcast messages, and unicast messages sent to this net instance
	 * address.
	 * 
	 * @param listener
	 */
	public void addNetListener(NetListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the list of registered listeners
	 * @param listener
	 * @see #addNetListener(NetListener)
	 */
	public void removeNetListener(NetListener listener) {
		listeners.remove(listener);
	}

	/*
	 * Methods below this point are reserved for net.internal classes. Do not
	 * call from user code.
	 */

	/**
	 * Forward unicast message to registered listeners. Method meant for internal
	 * use: do NOT call from user-defined code.
	 * 
	 * @param senderAddress
	 *            Sender's address
	 * @param obj
	 *            Message content
	 */
	public void notifyUnicastReception(Address senderAddress, Serializable obj) {
		for (NetListener listener : listeners) {
			listener.unicastReceived(senderAddress, obj);
		}
	}

	/**
	 * Forward broadcast message to registered listeners. Method meant for internal
	 * use: do NOT call from user-defined code.
	 * 
	 * @param senderAddress
	 *            Sender's address
	 * @param obj
	 *            Message content
	 */
	public void notifyBroadcastReception(Address senderAddress, Serializable obj) {
		for (NetListener listener : listeners) {
			listener.broadcastReceived(senderAddress, obj);
		}
	}

}
