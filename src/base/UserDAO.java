package base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import main.GamePanel;

public class UserDAO {

	public static int online = 0;
	public static int offline = 1;
	public static int inGame = 2;
	public static int inWaitingRoom = 3;
	
	private String username = null;
	private String hashedPassword = null;
	private String ipAddress = null;
	private int state = -1;
	
	// Privatni default konstruktor za povlacenje podataka iz baze
	private UserDAO() {
		
	}
	
	public UserDAO(String username, String password, int state) {
		setUsername(username);
		hashAndSetPassword(password);
		setState(state);
		setIpAddress();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getHashedPassword() {
		return hashedPassword;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public int getState() {
		return state;
	}
	
	public void setUsername(String username) {
		if (Utility.usernameIsValid(username)) {
			this.username = username;
			System.out.println("Username uspesno unet!");
		} else {
			System.out.println("Username nije ispravno unet!");
		}
	}
	
	// Koristimo kada zelimo da izvrsimo proveru 
	// Koristiti pri sign upovanju
	public void hashAndSetPassword(String password) {
		if (Utility.passwordIsValid(password)) {
			setHashedPassword(password);
		} else {
			System.out.println("Password nije ispravno unet!");
		}	
	}
	
	// Koristimo kada zelimo da izbegnemo proveru
	// Koristiti kada se pravi novi objekat UserDAO prilikom povlacenja iz baze
	private void setHashedPassword(String password) {
		this.hashedPassword = Utility.hashPassword(password);
		System.out.println("Password uspesno unet!");
	}
	
	public void setIpAddress() {
		String ip = GamePanel.getPublicIp();
		if (ip == null) {
			System.out.println("Greska u povlacenju javne ip adrese: ip = null");
			return;
		}
		this.ipAddress = ip;
	}
	
	public void setState(int state) {
		if (state >= 0 && state <= 5) {
			this.state = state;
			System.out.println("Status uspesno unet!");
		} else {
			System.out.println("Status nije uspesno unet!");
		}
	}
	
	public void addUser() {
		
        if (username != null && hashedPassword != null && state != -1) {
        	String query = "INSERT INTO user (username, password, state) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, this.username);
                stmt.setString(2, this.hashedPassword);
                stmt.setInt(3, this.state);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted ==  1) {
                    System.out.println("Korisnik uspesno dodat!");
                }

            } catch (SQLException e) {
            	System.out.println("Korisnik nije uspesno unet u bazu!");
                e.printStackTrace();
            }
        } else {
        	System.out.println("Korisnik nije uspesno unet u bazu!");
        }
    }
	
	public static UserDAO getUser(String username) {
		String query = "SELECT username, password, state FROM user WHERE username = ?";
        UserDAO user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String dbUsername = resultSet.getString("username");
                String dbHashedPassword = resultSet.getString("password");
                int dbState = resultSet.getInt("state");

                user = new UserDAO();             
                user.username = dbUsername;
                user.hashedPassword = dbHashedPassword;
                user.state = dbState;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user == null) {
        	System.out.println("User nije uspesno povucen!");
        } else {
        	System.out.println("User uspesno povucen!");
        }
        return user;
	}

	public static boolean updateUserState(String username, int newState) {
		String query = "UPDATE user SET state = ? WHERE username = ?";
        boolean updated = false;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newState);  
            stmt.setString(2, username);  

            int rowsUpdated = stmt.executeUpdate();
            updated = (rowsUpdated > 0);
            
            if(updated) {
            	System.out.println("Status uspesno apdejtovan: " + newState);
            }
            else {
            	System.out.println("Status nije apdejtovan");
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return updated;
    }
	
	public static boolean loginUser(String username) {
		return updateUserState(username, UserDAO.online);
	}
	
	public static boolean logoutUser(String username) {
		return updateUserState(username, UserDAO.offline);
	}
	
	public static LinkedList<String> getOnlineUsers() {
		String query = "SELECT username FROM user WHERE state = ?";
		LinkedList<String> usernames = new LinkedList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, UserDAO.online);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } 

        return usernames;
	}
	
	@Override
	public String toString() {
		return username + ":" + ipAddress;
	}

}
