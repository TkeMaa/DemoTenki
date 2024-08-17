package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import main.GamePanel;

public class GameClient extends Thread {

	// 12345 - port serverskog rutera za slusanje udp paketa
	private int serverPort;
	// 79.175.76.229 - ip adresa serverskog rutera
	private InetAddress serverAddress;
	
	private DatagramSocket socket;
	private GamePanel gp;
	
	public GameClient(GamePanel gp, String serverAddress, int serverPort) {
		this.gp = gp;
		this.serverPort = serverPort;
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
			String message = new String(packet.getData());
			System.out.println("SERVER [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] > " + message);
		}
	}
	
	public void sendData(String packetType, String data) {
		String dataPacket = packetType + ":" + data;
		DatagramPacket packet = new DatagramPacket(dataPacket.getBytes(), dataPacket.length(), serverAddress, serverPort);
		System.out.println("Paket u sendData-gameClient: " + dataPacket);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
