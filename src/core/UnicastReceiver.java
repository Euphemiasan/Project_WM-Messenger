package core;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class UnicastReceiver extends Thread {

	public static boolean verbose = false;
	private NetInterface net;
	private int port;
	private Address address;
	private ServerSocket serverSocket;
	private boolean accepting;
	private List<AcceptedConnectionThread> connections;

	/**
	 * Creates a TCP server socket, that automatically finds a free TCP port for
	 * the default address.
	 * 
	 * @param net
	 */
	public UnicastReceiver(NetInterface net, int preferredPort) {
		this.net = net;
		this.port = preferredPort;
		connections = new ArrayList<UnicastReceiver.AcceptedConnectionThread>(
				NetInterface.NB_UNICAST_CONNECTIONS);
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(Address.getDefaultIPAddress());
			try {
				serverSocket = new ServerSocket(port, NetInterface.NB_UNICAST_CONNECTIONS,
						inetAddress);
			} catch (SocketException e) {
				serverSocket = new ServerSocket(0, NetInterface.NB_UNICAST_CONNECTIONS,
						inetAddress);
				if (verbose) {
					System.out.println("Net: unicast port " + port
							+ " already used. Using "
							+ serverSocket.getLocalPort() + " instead.");
				}
				port = serverSocket.getLocalPort();
			}
			address = new Address(serverSocket.getInetAddress()
					.getHostAddress(), port);
			if (verbose) {
				System.out.println("Net: unicast receiver ready at " + address);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(40);
		}
		start();
	}

	@Override
	public void run() {
		// wait for incoming client connections
		accepting = true;
		try {
			while (accepting) {
				if (verbose) {
					System.out
							.println(("Net: unicast receiver server blocked on accept"));
				}
				Socket clientSocket = serverSocket.accept();
				connections.add(new AcceptedConnectionThread(clientSocket));
			}
		} catch (SocketException e) {
			if (verbose) {
				System.out
						.println(("Net: closing unicast receiver server socket... done"));
			}
			accepting = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Address getAddress() {
		return address;
	}

	public void close() {
		if (serverSocket != null) {
			if (verbose) {
				System.out
						.println(("Net: closing unicast receiver server socket..."));
			}
			try {
				serverSocket.close();
				for (AcceptedConnectionThread connection : connections) {
					connection.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class AcceptedConnectionThread extends Thread {

		private Socket clientSocket;
		private ObjectInputStream input;
		private Address senderAddress;
		private boolean connected;

		public AcceptedConnectionThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
			try {
				input = new ObjectInputStream(clientSocket.getInputStream());
				// the first message sent by the client is its application
				// address
				senderAddress = (Address) input.readObject();
				if (verbose) {
					System.out
							.println("Net: new unicast receiver connection to "
									+ senderAddress);
				}
				start();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		public void close() throws IOException {
			if (verbose) {
				System.out.println(("Net: closing unicast receiver socket..."));
			}
			clientSocket.close();
		}

		@Override
		public void run() {
			connected = true;
			while (connected) {
				Serializable obj;
				try {
					if (verbose) {
						System.out
								.println(("Net: unicast receiver blocked on readObject"));
					}
					obj = (Serializable) input.readObject();
					if (verbose) {
						System.out
								.println("Net: unicast receiver received message");
					}
					net.notifyUnicastReception(senderAddress, obj);
				} catch (EOFException e) {
					if (verbose) {
						System.out
								.println("Net: unicast receiver connection closed by client.");
					}
					connected = false;
				} catch (SocketException e) {
					if (verbose) {
						System.out
								.println(("Net: closing unicast receiver socket... done"));
					}
					connected = false;
				} catch (Exception e) {
					e.printStackTrace();
					connected = false;
				}
			}
			try {
				clientSocket.close();
				if (verbose) {
					System.out.println("Net: unicast receiver closed");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
