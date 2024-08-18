package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class BattleThread extends Thread {
	
	public GameServer gameServer = null;
	public OnlineUser player1 = null;
	public OnlineUser player2 = null;
	public DatagramSocket serverSocket = null;
	
	public BattleThread(OnlineUser player1, OnlineUser player2, GameServer gameServer) {
		this.player1 = player1;
		this.player2 = player2;
		this.gameServer = gameServer;
		
		try {
			this.serverSocket = new DatagramSocket(5001);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		gameServer.sendData(GameServer.setInBattleRowColsPacket, GameServer.player1Coordinates, player1.getIpAddress(), player1.getPort());
		gameServer.sendData(GameServer.setInBattleRowColsPacket, GameServer.player2Coordinates, player2.getIpAddress(), player2.getPort());
	}
	
	@Override
	public void run() {
		
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				serverSocket.receive(packet);
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
				case GameServer.stopPacket: {
					sendData(GameServer.stopPacket, dataString.trim(), destIp, packetPort);
					break;
				}
			}
			
		}
		
	}
	
	public void sendData(String packetType, String data, String userAddress, int userPort) {
		DatagramSocket socket = null;
		InetAddress inetAddress = null;
		
		try {
			
			socket = new DatagramSocket();
			inetAddress = InetAddress.getByName(userAddress);
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		
		String dataString = packetType + ":" + data;
		byte[] dataByte = dataString.getBytes();
		
		DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length, inetAddress, userPort);
		System.out.println("Paket u sendData-battleThread: " + dataString);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
