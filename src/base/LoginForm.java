package base;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import main.GamePanel;
import networking.GameServer;

public class LoginForm extends JFrame {

	private static final long serialVersionUID = 1L;
	
	GamePanel gp = null;

	private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginForm(GamePanel gp) {
        this.gp = gp;
        this.setResizable(false);
    	
    	setTitle("Log-in");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        panel.add(passwordField);
        
        panel.add(new JLabel());
        
        loginButton = new JButton("Log-in");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
            	String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (!Utility.usernameIsAvaliable(username)) {
                	if (Utility.usernameMatchesPassword(username, password)) {
	                	if (UserDAO.getUser(username).getState() == UserDAO.offline) {	
                			// Log-inujemo korisnika u bazi i saljemo login paket serveru
	                		if(UserDAO.loginUser(username)){
	                			System.out.println("Baza: Ulogovan");
	                		} else {
	                			System.out.println("Baza: Nije Ulogovan");
	                		}
	                		gp.user = new UserDAO(username, password, UserDAO.online);
	                		System.out.println("Paket u loginForm: " + gp.user.toString());
	                		
	                		// Prosledjujemo username, ip, port se dobija na serveru:
	                		gp.socketClient.sendDataToServer(GameServer.loginPacket, gp.user.toString());
	                		
	                		// Tranzicija u playState:
	                		gp.gameState = GamePanel.playState;
	                		
	                		gp.loginForm = null;
	                		dispose();
	                	} else {
	                		JOptionPane.showMessageDialog(
	                    			null, 
	                    			"Korisnik sa ovim username-om je trenutno online.", 
	                    			"Message", 
	                    			JOptionPane.INFORMATION_MESSAGE);
	        				System.out.println("Korisnik vec ulogovan!");
	                	}
                	}
                	else {
                		JOptionPane.showMessageDialog(
                    			null, 
                    			"Password koji ste uneli ne odgovara datom username-u.", 
                    			"Message", 
                    			JOptionPane.ERROR_MESSAGE);
        				System.out.println("Niste uneli odgovarajuci password!");
                	}
                } else {
                	JOptionPane.showMessageDialog(
                			null, 
                			"Uneti username ne postoji.", 
                			"Message", 
                			JOptionPane.ERROR_MESSAGE);
        			System.out.println("Uneti username ne postoji!");
                }
            }
        });

        getContentPane().add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
        
        // Postavi atribut "loginForm" gp-a na null prilikom gasenja log-in forme
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
	        	gp.loginForm = null;
	        	dispose(); 
            }
        });
   
    }
    
}