package base;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.GamePanel;

public class InvitePlayerForm extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	GamePanel gp;
	private JTextField invitedPlayer;
	private JButton inviteButton;
	
	public InvitePlayerForm(GamePanel gp) {
		this.gp = gp;
		
    this.setResizable(false);
    	
    	setTitle("Invite player into battle");
    	
        JPanel panel= new JPanel();
        panel.setLayout(new GridLayout(18,1));
        panel.setPreferredSize(new Dimension(400, 350));
        
        panel.add(new JLabel("Unesite username igraca:"));
        invitedPlayer = new JTextField(20);
        panel.add(invitedPlayer); 
    
        inviteButton = new JButton("Invite");
        panel.add(inviteButton);

        inviteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
            	String username = invitedPlayer.getText();
            	
            	// Username dostupan - igrac sa tim usernameom ne postoji
            	if(Utility.usernameIsAvaliable(username)) {
            		JOptionPane.showMessageDialog(
                			null, 
                			"Igrac sa unetim username-om ne postoji!", 
                			"Message", 
                			JOptionPane.ERROR_MESSAGE);
        			System.out.println("Igrac sa unetim username-om ne postoji!");
            	} else {
            		// Igrac ne moze da pozove samog sebe
            		if (!username.equals(gp.user.getUsername())) {
            			System.out.println("Username pozvanog igraca: " + invitedPlayer.getText());
            			System.out.println("Uslo se u if, username user-a: " + gp.user.getUsername());
            			if(UserDAO.getUser(username).getState() == UserDAO.offline) {
                			JOptionPane.showMessageDialog(
                        			null, 
                        			"Igrac sa unetim username-om je offline!", 
                        			"Message", 
                        			JOptionPane.ERROR_MESSAGE);
                			System.out.println("Igrac sa unetim username-om je offline!");
                		} 

                		if(UserDAO.getUser(username).getState() == UserDAO.inGame) {
                			JOptionPane.showMessageDialog(
                        			null, 
                        			"Igrac sa unetim username-om je u game-u!", 
                        			"Message", 
                        			JOptionPane.ERROR_MESSAGE);
                			System.out.println("Igrac sa unetim username-om je u game-u!");
                		} 
                		
                		if(UserDAO.getUser(username).getState() == UserDAO.online) {
                			// POSLATI PORUKU DRUGOM IGRACU
                			JOptionPane.showMessageDialog(
                        			null, 
                        			"Igrac (" + username + ") je pozvan u borbu!", 
                        			"Message", 
                        			JOptionPane.INFORMATION_MESSAGE);
                			System.out.println("Igrac je pozvan!");
                		}
            		} 
            	}
            
            }
        });

        getContentPane().add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
        
        // Postavi atribut "signupForm" gp-a na null prilikom gasenja sign-up forme
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	gp.invitePlayerForm = null;
            	dispose();
            }
        });
        
    }
}
	
