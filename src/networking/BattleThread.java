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
		
		gameServer.sendData(GameServer.setInBattleRowColsPacket, GameServer.player1Coordinates, player1.getIpAddress(), player1.getPort());
		gameServer.sendData(GameServer.setInBattleRowColsPacket, GameServer.player2Coordinates, player2.getIpAddress(), player2.getPort());
	}
	
	@Override
	public void run() {
		
		long timer = 0;
		long lastTime = System.currentTimeMillis();	
		
		while (true) {
			
			long currTime = System.currentTimeMillis();
			timer += (currTime - lastTime);
			if (timer >= 3000000) {
				System.out.println("Battle thread is running...");
				timer = 0;
			}
			lastTime = currTime;
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
		System.out.println("Paket u sendData-gameClient: " + dataString);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
