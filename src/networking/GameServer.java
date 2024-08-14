package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.GamePanel;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private GamePanel gp;
	
	public GameServer(GamePanel gp) {
		this.gp = gp;
		try {
			
			this.socket = new DatagramSocket(5000);
			
		} catch (SocketException e) {
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
			String message = new String(packet.getData());
			System.out.println("CLIENT [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] > " + message);
			if(message.trim().equalsIgnoreCase("ping")){
				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			}
		}
	}
	
	public void sendData(byte[] data, InetAddress ipAdress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
