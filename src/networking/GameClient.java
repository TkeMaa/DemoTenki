package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import main.GamePanel;

public class GameClient extends Thread {

	private InetAddress ipAdress;
	private DatagramSocket socket;
	private GamePanel gp;
	
	public GameClient(GamePanel gp, String ipAddress) {
		this.gp = gp;
		try {
			
			this.socket = new DatagramSocket();
			this.ipAdress = InetAddress.getByName(ipAddress);
			
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
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, 5000);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
