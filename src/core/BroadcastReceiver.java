package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class BroadcastReceiver extends Thread {

	public static boolean verbose = false;
	private boolean active;
	private NetInterface net;
	private DatagramSocket socket;
	private int port;

	public BroadcastReceiver(NetInterface net) {
		this.net = net;

		// find a free UDP port for this application, in case several
		// applications run on the same machine
		port = NetInterface.BROADCAST_PORT;
		int i = 0;
		boolean portOK = false;
		while (!portOK && i < NetInterface.NB_BROADCAST_PORTS) {
			try {
				// FIXME: cannot use the Inet address of the first connected
				// interface
				// socket = new DatagramSocket(port,
				// net.getAddress().getInetAddress());
				// String ipAddress = socket.getLocalAddress().getHostAddress();
				socket = new DatagramSocket(port);
				portOK = true;
			} catch (SocketException e) {
				// port already in use, try next
				port++;
				i++;
			}
		}
		if (!portOK) {
			System.out.println("No broadcast port available.");
			System.exit(30);
		}
		if (verbose) {
			System.out.println("Net: broadcast receiver ready on port " + port);
		}
		start();
	}

	public void close() {
		if (socket != null) {
			if (verbose) {
				System.out
						.println(("Net: closing broadcast receiver socket..."));
			}
			socket.close();
		}
	}

	@Override
	public void run() {
		// loop to receive broadcasted UDP packets
		active = true;
		DatagramPacket packet = new DatagramPacket(
				new byte[NetInterface.UDP_PACKET_SIZE],
				NetInterface.UDP_PACKET_SIZE);
		while (active) {
			try {
				// blocks thread until reception of a packet
				if (verbose) {
					System.out
							.println(("Net: broadcast receiver blocked on receive"));
				}
				socket.receive(packet);
				if (verbose) {
					System.out.println("Net: broadcast receiver received "
							+ packet.getLength() + " bytes");
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(
						packet.getData());
				ObjectInputStream ois = new ObjectInputStream(bais);
				Address senderAddress = (Address) ois.readObject();
				Serializable obj = (Serializable) ois.readObject();
				net.notifyBroadcastReception(senderAddress, obj);
			} catch (SocketException e) {
				// socket closed by call to close()
				active = false;
				if (verbose) {
					System.out
							.println(("Net: closing broadcast receiver socket... done"));
				}
			} catch (IOException e) {
				System.out
						.println("Net: broadcast receiver closing after unexpected IO exception");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
