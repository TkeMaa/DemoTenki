package main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import java.awt.Image;
//import javax.swing.ImageIcon;
import javax.swing.JFrame;

import base.UserDAO;

public class Main {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		GamePanel gamePanel = new GamePanel();
		
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
		
		gamePanel.startGameThread();
	}
	
}
