package networking;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import base.UserDAO;
import entity.EnemyPlayer;
import main.GamePanel;
import main.Sound;

public class GameClient extends Thread {

	// *Ogranicenje na samo jednu partiju istovremeno*
	// 12345 - port serverskog rutera za slusanje udp paketa koji se salju na server
	private int serverPort;
	// 12346 - port serverskog rutera za slusanje udp paketa koji se salju na battleThread
	private int battlePort;
	// 79.175.76.229 - ip adresa serverskog rutera
	private InetAddress serverAddress;
	
	private DatagramSocket socket;
	private GamePanel gp;
	
	public GameClient(GamePanel gp, String serverAddress, int serverPort, int battlePort) {
		this.gp = gp;
		this.serverPort = serverPort;
		this.battlePort = battlePort;
		
		try {
			
			this.socket = new DatagramSocket();
			this.serverAddress = InetAddress.getByName(serverAddress);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		// Receive data
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String message = (new String(packet.getData())).trim();
			System.out.println("Primljeni paket: " + message + "+");
			// Packet format - XX:data
			//                 012
			String packetType = message.substring(0,2);
			String dataString = message.substring(3).trim();
			
			String packetIp = packet.getAddress().getHostAddress();
			int packetPort = packet.getPort();
			
			switch (packetType) {
				
				case GameServer.changeDirectionPacket: {
					System.out.println("Primljen paket: " + message);
					if (dataString.trim().equalsIgnoreCase(GameServer.upDir)) {
						gp.enemy.direction = "up";
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.downDir)) {
						gp.enemy.direction = "left";
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.leftDir)) {
						gp.enemy.direction = "right";
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.rightDir)) {
						gp.enemy.direction = "down";
					}
					break;
				}
				case GameServer.movePacket: {
					System.out.println("Primljen paket: " + message);
					if (dataString.trim().equalsIgnoreCase(GameServer.movedUp)) {
						gp.enemy.worldY -= gp.enemy.speed;
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.movedDown)) {
						gp.enemy.worldY += gp.enemy.speed;
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.movedLeft)) {
						gp.enemy.worldX -= gp.enemy.speed;
					}
					if (dataString.trim().equalsIgnoreCase(GameServer.movedRight)) {
						gp.enemy.worldX += gp.enemy.speed;
					}
					break;
				}
//				case GameServer.stopPacket: {
//					gp.enemy.moved_up = false;
//					gp.enemy.moved_down = false;
//					gp.enemy.moved_left = false;
//					gp.enemy.moved_right = false;
//					break;
//				}
				
				case GameServer.testPacket: {
					System.out.println("SERVER [" + packetIp + ":" + packetPort + "] > " + dataString);
					break;
				}
				case GameServer.invitedPacket: {
					String[] userInfo = dataString.split(":");
					for (int i = 0; i < userInfo.length; i++) {
						System.out.print(userInfo[i]);
					}
					String invitedPlayer = userInfo[0].trim();
					String inviteFromPlayer = userInfo[1].trim();
					
					String poruka = "Igrac " + inviteFromPlayer + " Vas je pozvao u igru(Vas username: " + invitedPlayer + ")";
					System.out.println(poruka);
					
					// Apdejtuj user-ov status na inWaitingRoom
					gp.user.setState(UserDAO.inWaitingRoom);
					UserDAO.updateUserState(gp.user.getUsername(), UserDAO.inWaitingRoom);
					
					StringBuilder responseYes = new StringBuilder();
					responseYes.append(invitedPlayer);
					responseYes.append(":");
					responseYes.append(inviteFromPlayer);
					responseYes.append(":yes");
					
					// Saljemo odgovor na invite drugom klijentu
					if (JOptionPane.showConfirmDialog(gp, 
							poruka + ", da li prihvatate poziv?", 
							"Invite from " + inviteFromPlayer, 
							JOptionPane.YES_NO_OPTION) != 0) {
						
						StringBuilder responseNo = new StringBuilder();
						responseNo.append(invitedPlayer);
						responseNo.append(":");
						responseNo.append(inviteFromPlayer);
						responseNo.append(":no");
						
						sendDataToServer(GameServer.responsePacket, responseNo.toString());
						
						gp.user.setState(UserDAO.online);
						UserDAO.updateUserState(gp.user.getUsername(), UserDAO.online);
						break;
					}
					
					System.out.println("[CLIENT] response: " + responseYes.toString().trim());
					
					sendDataToServer(GameServer.responsePacket, responseYes.toString().trim());
					
					break;
				}
				case GameServer.exitWaitingRoomPacket: {
					if (dataString.trim().equalsIgnoreCase(GameServer.exit)) {
						System.out.println("[CLIENT " + ManagementFactory.getRuntimeMXBean().getName() + "]Primio sam exit paket: " + dataString.trim());
					}
					// Oslobodi user-a iz waiting room-a
					gp.user.setState(UserDAO.online);
					UserDAO.updateUserState(gp.user.getUsername(), UserDAO.online);
					break;
				}
				case GameServer.setInBattleRowColsPacket: {
					
					String[] rowColInfo = dataString.split(":");
					System.out.println();
					int row = Integer.parseInt(rowColInfo[0].trim());
					int col = Integer.parseInt(rowColInfo[1].trim());
					gp.player.setDefaultCoordinates(row, col);
					gp.enemy = new EnemyPlayer(gp);
					if(row == 1) {
						gp.enemy.worldX = 33 * GamePanel.tileSize;
						gp.enemy.worldY = 33 * GamePanel.tileSize;
					} else {
						gp.enemy.worldX = 1 * GamePanel.tileSize;
						gp.enemy.worldY = 1 * GamePanel.tileSize;
					}
					
					gp.stopMusic();
					gp.playMusic(Sound.battleMusic);
					gp.gameState = GamePanel.inGameState;
					break;
				}
			}
			data = null;
		}
			
	}
	
	public void sendDataToServer(String packetType, String data) {
		String dataString = packetType + ":" + data;
		byte[] dataByte = dataString.getBytes();
		DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, serverAddress, serverPort);
		System.out.println("Paket u sendData-gameClient: " + dataString);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDataToBattleThread(String packetType, String data) {
		String dataString = packetType + ":" + data;
		byte[] dataByte = dataString.getBytes();
		DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, serverAddress, battlePort);
		System.out.println("Paket u sendData-gameClient: " + dataString);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
