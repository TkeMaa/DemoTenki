package main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

//import java.awt.Image;
//import javax.swing.ImageIcon;
import javax.swing.JFrame;

import base.DatabaseConnection;
import base.UserDAO;

public class Main {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		GamePanel gamePanel = new GamePanel();
		
		// Provera konekcije
		try {
			Connection conn = DatabaseConnection.getConnection();
			if (conn != null) {
				System.out.println("Konekcija sa bazom uspesno uspostavljena.");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Odloguj korisnika prilikom gasenja 
		window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gamePanel.user != null) {
                	System.out.println("Logging out the user...");
                	System.out.println("Username: " + gamePanel.user.getUsername());
                	if(UserDAO.logoutUser(gamePanel.user.getUsername())) {
                		System.out.println("User je uspesno odlogovan");
                	}
                	else {
                		System.out.println("User nije odlogovan");
                	}
                	window.dispose(); 
                	System.exit(0); 
                }
            }
		});
//		ImageIcon icon = new ImageIcon("/player/plavi_gore1.png");
//		Image iconImage = (BufferedImage) icon.getImage();
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Tenki");	
		
		window.add(gamePanel);
		window.pack(); // Omogucava da se JFrame postavi na velicinu gamePanela

//		window.setIconImage(iconImage);
		
		window.setLocationRelativeTo(null); //Prozor ce biti postavljen u centar ekrana
		window.setVisible(true);
		
		// Odloguj korisnika ako se dogodi neka greska
		try {
			gamePanel.startGameThread();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Logging out the user...");
        	System.out.println("Username: " + gamePanel.user.getUsername());
        	if(UserDAO.logoutUser(gamePanel.user.getUsername())) {
        		System.out.println("User je uspesno odlogovan");
        	}
        	else {
        		System.out.println("User nije odlogovan");
        	}
        	window.dispose(); 
        	System.exit(0);
		}
	}
	
}
