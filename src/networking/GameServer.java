package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import main.GamePanel;

public class GameServer extends Thread {

	// PACKET TYPES
	public static final String loginPacket = "00"; // 00:username:ip - pa postavi port
	public static final String testPacket = "01"; // 01:data
	
	public static final String invitePacket = "02"; // 02:usernameInvited:usernameInviteFrom
	public static final String invitedPacket = "03"; // 03:usernameInvited:usernameInviteFrom
	
	public static final String responsePacket = "04"; // 04:usernameFrom:usernameResponseTo:response
		public static final String yesResponse = "0";
		
	public static final String exitWaitingRoomPacket = "05"; // 05:exit
		public static final String exit = "exit";
		
	public static final String setInBattleRowColsPacket = "06";
		public static final String player1Coordinates = "1:1";
		public static final String player2Coordinates = "33:33";
	
	private DatagramSocket socket;
	private GamePanel gp;
	private final int portNum = 5000;
	
	public List<OnlineUser> onlineUsers;
	public Thread battleThread;
	
	private OnlineUser getOnlineUserByUsername(String username) {
		for (int i = 0; i < onlineUsers.size(); i++) {
			if (onlineUsers.get(i).getUsername().equals(username)) {
				return onlineUsers.get(i);
			}
		}
		return null;
	}
	
	private void printOnlineUsers() {
		for (int i = 0; i < onlineUsers.size(); i++) {
			System.out.println((i + 1) + ". " + "username: " + onlineUsers.get(i).getUsername() + 
					" | public ipv4: " + onlineUsers.get(i).getIpAddress() + "|" + onlineUsers.get(i).getPort());
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
			printOnlineUsers();
			
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
			
			String packetIp = packet.getAddress().getHostAddress();
			int packetPort = packet.getPort();
			
			// Proveravamo prve dve cifre primljenog paketa kako bismo utvrdili njegov tip
			switch (packetType) {
				// 00
				// Ubaci korisnika u listu onlineUsers:
				case GameServer.loginPacket: {
					String[] userInfo = dataString.split(":");
					
					OnlineUser connectedUser = new OnlineUser(userInfo[0].trim(), userInfo[1].trim());
					connectedUser.setPort(packetPort);
					onlineUsers.add(connectedUser);
					
					System.out.println("Korisnik dodat u listu onlajn korisnika servera.");
					break;
				}
				// 01
				// Zasto mu ne vracas pong? - MORA trim().equalsIgnoreCase
				// Test ping-pong:
				case GameServer.testPacket: {
					System.out.println("CLIENT [" + packetIp + ":" + packetPort + "] > " + message);
					if(dataString.trim().equalsIgnoreCase("ping")){
						sendData(GameServer.testPacket,"pong", packetIp, packetPort);
					}
					break;
				}
				// 02
				// Posalji invite pozvanom igracu
				// 02:usernameInvited:usernameInviteFrom
				case GameServer.invitePacket: {
					
					String[] userInfo = dataString.split(":");
					OnlineUser pom = getOnlineUserByUsername(userInfo[0]);
					
					if (pom != null) {
						
						sendData(GameServer.invitedPacket, dataString, pom.getIpAddress(), pom.getPort());
						
					} else {
						System.out.println("Korisnik nedostupan");
					}
					break;
				}
				
				case GameServer.responsePacket: {
					
					System.out.println(dataString);
					String[] response = dataString.split(":");
					String responseFrom = response[0];
					String responseTo = response[1];
					String responseOption = response[2];
					
					if (!responseOption.trim().equalsIgnoreCase("yes")) {
						OnlineUser pom = getOnlineUserByUsername(responseTo);
						if (pom != null) {
							sendData(GameServer.exitWaitingRoomPacket, 
										GameServer.exit.trim(), 
										pom.getIpAddress(),
										pom.getPort());
						}
					} else {
						BattleThread battleThread = new BattleThread(getOnlineUserByUsername(responseTo), getOnlineUserByUsername(responseFrom), this);
						battleThread.start();
					}
					
				}
				
			}
			
		}
		
	}
	
	public void sendData(String packetType, String data, String ipAdress, int port) {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(ipAdress);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		String dataString = (packetType + ":" + data).trim();
		System.out.println("Paket u sendData-gameServer: " + dataString);
		
		byte[] dataByte = dataString.getBytes();
		
		DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, inetAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
}
