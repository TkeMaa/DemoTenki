package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

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
		
	public static final String changeDirectionPacket = "07"; // 07:dir
		public static final String upDir = "up"; 
		public static final String downDir = "down";
		public static final String leftDir = "left";
		public static final String rightDir = "right";
	
	public static final String movePacket = "08";	// 08:mov
		public static final String movedUp = "up";
		public static final String movedDown = "down";
		public static final String movedLeft = "left";
		public static final String movedRight = "right";
		
	public static final String damageTilePacket = "09"; // 09:row:col:imgNum
	
	public static final String createEnemyProjectilePacket = "10"; // 10:x:y:dir
	public static final String stopEnemyProjectilePacket = "11"; // 11:hitEnemy(0 - true, 1 - false)
	
	public static final String youWinPacket = "12"; // 12:
		
	public boolean battleRunning = false;
		
	private DatagramSocket socket;
	private GamePanel gp;
	private final int portNum = 5000;
	
	public OnlineUser player1 = null;
	public OnlineUser player2 = null;
	
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
	
	// TODO: Promeniti u TCP
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
			if (battleRunning) {
				String destIp = null;
				int destPort = 0;
				
				if (packetPort == player1.getPort()) {
					destPort = player2.getPort();
				} else {
					destPort = player1.getPort();
				}
				
				if (packetIp.trim().equalsIgnoreCase(player1.getIpAddress().trim())) {
					destIp = player2.getIpAddress().trim();
				} else {
					destIp = player1.getIpAddress().trim();
				}
				
				System.out.println("Packet received from: " + packetIp + "|" + packetPort);
				
				// Proveravamo prve dve cifre primljenog paketa kako bismo utvrdili njegov tip
				switch (packetType) {
					case GameServer.changeDirectionPacket: {
						sendData(GameServer.changeDirectionPacket, dataString.trim(), destIp, destPort);
						break;	
					}
					case GameServer.movePacket: {
						sendData(GameServer.movePacket, dataString.trim(), destIp, destPort);
						break;
					}
					case GameServer.damageTilePacket: {
						sendData(GameServer.damageTilePacket, dataString.trim(), destIp, destPort);
						break;
					}
					case GameServer.createEnemyProjectilePacket: {
						sendData(GameServer.createEnemyProjectilePacket, dataString.trim(), destIp, destPort);
						break;
					}
					case GameServer.stopEnemyProjectilePacket: {
						sendData(GameServer.stopEnemyProjectilePacket, dataString.trim(), destIp, destPort);
						break;
					}
					case GameServer.youWinPacket: {
						sendData(GameServer.youWinPacket, dataString.trim(), destIp, destPort);
						battleRunning = false;
						break;
					}
				}
			} else {
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
						
						player1 = getOnlineUserByUsername(responseTo);
						player2 = getOnlineUserByUsername(responseFrom);
						
						battleRunning = true;
						
						sendData(GameServer.setInBattleRowColsPacket, GameServer.player1Coordinates, player1.getIpAddress(), player1.getPort());
						sendData(GameServer.setInBattleRowColsPacket, GameServer.player2Coordinates, player2.getIpAddress(), player2.getPort());
						
					}
					break;
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
		System.out.println("Paket u sendData-gameServer: " + dataString + "\nSending data to: " + inetAddress.getHostAddress() + " | " + port);
		
		byte[] dataByte = dataString.getBytes();
		
		DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, inetAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
}
