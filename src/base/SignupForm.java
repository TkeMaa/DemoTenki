package base;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.GamePanel;

public class SignupForm extends JFrame{

	private static final long serialVersionUID = 1L;

	GamePanel gp = null;

	private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton;

    public SignupForm(GamePanel gp) {
        this.gp = gp;
        this.setResizable(false);
    	
    	setTitle("Sign-up");
    	
        JPanel panel= new JPanel();
        panel.setLayout(new GridLayout(18,1));
        panel.setPreferredSize(new Dimension(400, 350));
        
        panel.add(new JLabel("Username MORA biti duzine izmedju 5 - 10 karaktera")).setForeground(Color.red);
        panel.add(new JLabel("Username MOZE da sadrzi:")).setForeground(Color.red);
        panel.add(new JLabel("*Mala slova")).setForeground(Color.red);
        panel.add(new JLabel("*Brojeve")).setForeground(Color.red);
        panel.add(new JLabel("*Karaktere: \".\" i \"_\"")).setForeground(Color.red); 
        
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        panel.add(usernameField);
        usernameField.setBounds(120, 120, 23, 23);
        
        panel.add(new JLabel("Password MORA biti duzine izmedju 5 - 10 karaktera")).setForeground(Color.red);
        panel.add(new JLabel("Password MORA da sadrzi:")).setForeground(Color.red);
        panel.add(new JLabel("*Bar jedno veliko slovo")).setForeground(Color.red);
        panel.add(new JLabel("*Bar jedno malo slovo")).setForeground(Color.red);
        panel.add(new JLabel("*Bar jednu cifru")).setForeground(Color.red);
        panel.add(new JLabel("*Bar jedan specijalni karakter(. * ! ?)")).setForeground(Color.red);
        panel.add(new JLabel()); 
      

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        panel.add(passwordField);
        
        panel.add(new JLabel());
       
        
        signupButton = new JButton("Sign-up");
        panel.add(signupButton);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
            	String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                // Proveravamo da li je uneti username dostupan
                if (Utility.usernameIsAvaliable(username)) {
                	// Pravimo pomocnog usera za proveru parametara
                	UserDAO pom = new UserDAO(username, password, UserDAO.offline);
                	// Proveravamo validnost parametara
                	if (pom.getUsername() == null ||
            			pom.getHashedPassword() == null ||
            			pom.getState() == -1) {
                		
                		JOptionPane.showMessageDialog(
                    			null, 
                    			"Uneti parametri nisu dobri, pokusajte ponovo.", 
                    			"Message", 
                    			JOptionPane.ERROR_MESSAGE);
            			System.out.println("Uneti parametri nisu dobri!");
                	} else {
                		pom.addUser();
                		System.out.println("Uspesno ste se registrovali!");
                		JOptionPane.showMessageDialog(
                    			null, 
                    			"Uspesno ste se registrovali!", 
                    			"Success",
                    			JOptionPane.INFORMATION_MESSAGE);
                		gp.signupForm = null;
                		dispose();
                	}
                } else {
                	JOptionPane.showMessageDialog(
                			null, "Uneti username vec postoji!", 
                			"Message", 
                			JOptionPane.ERROR_MESSAGE);
        			System.out.println("Uneti username vec postoji!");
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
            	
            	gp.signupForm.dispose();
            	gp.signupForm = null;
            }
        });
        
    }
    
}

