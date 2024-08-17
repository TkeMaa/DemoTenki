package networking;

public class OnlineUser {
	
	private String username;
	private String ipAddress;
	
	public OnlineUser(String username, String ipAddress) {
		setUsername(username);
		setIpAddress(ipAddress);
	}
	
	public String getUsername() {
		return username;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
