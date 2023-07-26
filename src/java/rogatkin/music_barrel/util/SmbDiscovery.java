package rogatkin.music_barrel.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SmbDiscovery {

	public static boolean checkPort(String host, int port) {
		int timeout = 200;
	
		try {
	          Socket socket = new Socket();
	          socket.connect(new InetSocketAddress(host, port), timeout);
	          socket.close();
	          return true;
	        } catch (Exception ex) {
	        
	        }
	    return false;      
	}

	public static String baseNet() {
		// InetAddress 
	
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static String[] getSmbHost() {
		try {
		InetAddress[] iaddress
        = InetAddress.getAllByName("localhost");
		} catch(Exception e) {
			
		}
		return null;
	}

}
