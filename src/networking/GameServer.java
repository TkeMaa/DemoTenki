package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import main.GamePanel;

public class GameServer extends Thread {

	// PACKET TYPES
	public static final String loginPacket = "00";
	public static final String testPacket = "01";
	
	private DatagramSocket socket;
	private GamePanel gp;
	private final int portNum = 5000;
	
	public List<OnlineUser> onlineUsers;
	
	private void printOnlineUsers() {
		for (int i = 0; i < onlineUsers.size(); i++) {
			System.out.println((i + 1) + ". " + "username: " + onlineUsers.get(i).getUsername() + 
					" | public ipv4: " + onlineUsers.get(i).getIpAddress());
		}
	}
	
	public GameServer(GamePanel gp) {
		this.gp = gp;
		this.onlineUsers = new LinkedList<OnlineUser>();
		try {
			// Pravimo soket za osluskivanje na portu 5000 na lokalnom racunaru;
			this.socket = new DatagramSocket(portNum);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		// Receive data
		while (true) {
			// TESTIRAJ LISTU onlineUsers,
			// Ovo se ispisuje samo kad se primi paket, posto server ceka da primi paket:
			// printOnlineUsers();
			
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String message = new String(packet.getData());
			// Packet format - XX:data
			//                 012
			String packetType = message.substring(0,2);
			String dataString = message.substring(3);
			// Proveravamo prve dve cifre primljenog paketa kako bismo utvrdili njegov tip
			switch (packetType) {
				// 00
				// Ubaci korisnika u listu onlineUsers:
				case GameServer.loginPacket: {
					String[] userInfo = dataString.split(":");
					onlineUsers.add(new OnlineUser(userInfo[0], userInfo[1]));
					System.out.println("Korisnik dodat u listu onlajn korisnika servera.");
					break;
				}
				// 01
				// Zasto mu ne vracas pong? - MORA trim().equalsIgnoreCase
				// Test ping-pong:
				case GameServer.testPacket: {
					System.out.println("CLIENT [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] > " + message);
					if(message.trim().equalsIgnoreCase("01:ping")){
						sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
					}
					break;
				}
			}
			
		}
		
	}
	
	public void sendData(byte[] data, InetAddress ipAdress, int port) {
		System.out.println("Paket u sendData-gameServer: " + new String(data));
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	
}
