package core;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Address of a NetInterface instance
 * @author t.perennou
 */
public class Address implements Serializable, Comparable<Address> {

	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private int tcpPort;
	private InetAddress inetAddress;
	private String pseudo;

	/**
	 * Creates an address with a String matching 'ipAddress:port', e.g. 192.168.0.1:40000
	 * @throws IllegalArgumentException if the format is wrong or the IP address is bad
	 * @param address
	 * @see NetInterface#getAddress()
	 * @see NetInterface#sendUnicast(Serializable, Address)
	 * @see NetListener#broadcastReceived(Address, Serializable)
	 * @see NetListener#unicastReceived(Address, Serializable)
	 */
	public Address(String address) {
		try {
			String[] words = address.split(":");
			this.ipAddress = words[0];
			this.tcpPort = Integer.parseInt(words[1]);
			inetAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(
					ipAddress
							+ " is not a valid IP address. Should be x.y.z.t, with 0 <= x,y,z,t <= 255.");
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Cannot parse address '"
					+ address
					+ "': should be 'ipAddress:port', e.g. '127.0.0.1:40000'");
		}
	}

	/**
	 * Creates an address with an IP address (as a String) and a TCP port. Useless for user-defined code.
	 * @throws IllegalArgumentException if the IP address is bad
	 * @param ipAddress
	 * @param tcpPort
	 */
	public Address(String ipAddress, int tcpPort) {
		this.ipAddress = ipAddress;
		this.tcpPort = tcpPort;
		try {
			inetAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(
					ipAddress
							+ " is not a valid IP address: should be x.y.z.t, with 0 <= x,y,z,t <= 255.");
		}
	}

	/**
	 * IP address part of that address
	 */
	public String getIpAddress() {
		return inetAddress.getHostAddress();
	}

	/**
	 * Port part of the address
	 */
	public int getTcpPort() {
		return tcpPort;
	}
	
	/**
	 * Internal java.io object representing the IP address
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	/* Goodies : hostname (short and long), physical network interface name */

	/**
	 * Short host name, e.g. aneto or localhost
	 */
	public String getHostName() {
		return inetAddress.getHostName();
	}

	/**
	 * Fully qualified host name, e.g. aneto.isae.fr
	 */
	public String getCanonicalHostName() {
		return inetAddress.getCanonicalHostName();
	}

	/**
	 * System name of the physical interface if the address is local. For Ethernet,
	 * you'll get 'eth0' on Linux, 'en0' on MacOSX. For WiFi, 'wlan0' or 'en1'
	 * @return Local physical interface name, or 'not on this machine'
	 */
	public String getNetworkInterfaceName() {
		try {
			NetworkInterface nif = NetworkInterface.getByInetAddress(inetAddress);
			if (nif != null) {
				return nif.getDisplayName();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "not on this machine";
	}

	/**
	 * The address as text, in the form needed by the String-based constructor, e.g. 192.168.0.1:40000
	 */
	@Override
	public String toString() {
		return ipAddress + ":" + tcpPort;
	}

	/**
	 * Full description of this address (IP address, DNS name and physical interface name if
	 * the address is on the machine where the program runs.
	 */
	public String description() {
		StringBuilder sb = new StringBuilder();
		sb.append("PHY interface: " + getNetworkInterfaceName() + "\n");
		sb.append("IP address: " + getIpAddress() + "\n");
		sb.append("Name: " + getHostName() + "\n");
		sb.append("Full Name: " + getCanonicalHostName() + "\n");
		sb.append("TCP port: " + tcpPort + "\n");
		return sb.toString();
	}
	
	/**
	 * Tests if the address is a loopback address, meaning that the application is not connected
	 * to a real network. In that case it is not possible to send broadcast messages.
	 */
	public boolean isLoopback() {
		return inetAddress.isLoopbackAddress();
	}

	/**
	 * Hash code of the address, only based on IP address and TCP port.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + tcpPort;
		return result;
	}

	/**
	 * Two addresses are equal if they have the same IP address and TCP port.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (tcpPort != other.tcpPort)
			return false;
		return true;
	}

	/**
	 * Addresses are ordered first by IP address then by TCP port.
	 */
	@Override
	public int compareTo(Address o) {
		if (ipAddress.compareTo(o.ipAddress) == 0) {
			if (tcpPort < o.tcpPort) {
				return -1;
			} else if (tcpPort > o.tcpPort) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return ipAddress.compareTo(o.ipAddress);
		}
	}

	/**
	 * IP4 addresses of the local host. Does not include "127.0.0.1". Only
	 * active devices: if Wifi is not activated you won't see it. If no Ethernet
	 * cable is plugged in, you won't see it.
	 */
	public static List<String> getLocalIPAddresses() {
		List<String> addresses = new ArrayList<String>();
		List<NetworkInterface> nifs = null;
		try {
			nifs = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : nifs) {
				// Bug fix: do not keep addresses for pseudo-devices like vmnet0
				// These pseudo-devices are used on Linux machines in ISAE labs
				// They are set upon installation of the VMware player software
				if (!nif.isLoopback() && !nif.getName().startsWith("vmnet")) {
					for (InetAddress addr : Collections.list(nif
							.getInetAddresses())) {
						if (addr instanceof Inet4Address)
							addresses.add(addr.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return addresses;
	}

	/**
	 * IP address of the first connected device (Ethernet or Wifi), or 127.0.0.1 (loopback)
	 */
	public static String getDefaultIPAddress() {
		String address = "127.0.0.1";
		List<String> localAddresses = getLocalIPAddresses();
		if (localAddresses.size() > 0) {
			address = localAddresses.get(0);
		}
		return address;
	}
	
	/**
	 * IP address of the connected device with the specified name.
	 * Examples: 'eth0' for Ethernet on Linux, 'en0' for Ethernet on MacOSXs
	 * @param name Device name 
	 */
	public static String getIPAddressByInterfaceName(String name) {
		String address = "NOT FOUND";
		try {
			NetworkInterface nif = NetworkInterface.getByName(name);
			for (InetAddress addr : Collections.list(nif
					.getInetAddresses())) {
				if (addr instanceof Inet4Address) {
					address = addr.getHostAddress();
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return address;
	}

}
