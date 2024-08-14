package base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.passay.*;

public class Utility {
	
	private static String allowedPasswordChars = "a-zA-Z0-9.*!?";
	private static String allowedUsernameChars = "a-z0-9_.";
	
	private static PasswordValidator passwordValidator = new PasswordValidator(				
		new AllowedRegexRule("^[" + allowedPasswordChars + "]+$"), // Dozvoljeni karakteri
	    new CharacterRule(EnglishCharacterData.UpperCase, 1), // Bar 1 veliko slovo
	    new CharacterRule(EnglishCharacterData.LowerCase, 1), // Bar 1 malo slovo 
	    new CharacterRule(EnglishCharacterData.Digit, 1), // Bar 1 broj
	    new CharacterRule(EnglishCharacterData.Special, 1), // Bar 1 specijalni karakter
	    new WhitespaceRule(), // Bez praznih mesta
	    new LengthRule(5, 10) // Duzina mora biti izmedju 5 i 10
	);
	
	private static PasswordValidator usernameValidator = new PasswordValidator(
		new AllowedRegexRule("^[" + allowedUsernameChars + "]+$"), // Dozvoljeni karakteri
	    new CharacterRule(EnglishCharacterData.LowerCase, 1), // Bar 1 malo slovo 
	    new WhitespaceRule(), // Bez praznih mesta
	    new LengthRule(5, 10) // Duzina mora biti izmedju 5 i 10
	);
	
	public static boolean passwordIsValid(String password) {
        return passwordValidator.validate(new PasswordData(password)).isValid();	
	}
	
	public static boolean usernameIsValid(String username) {
		return usernameValidator.validate(new PasswordData(username)).isValid();
	}
	
	public static boolean usernameIsAvaliable(String username) {
		String query = "SELECT username FROM user WHERE username = ?";
        boolean isAvailable = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            
            /* ResultSet pozicionira kursor iznad reda.
             * Sa resultSet.next() pomeramo kursor na odgovarajuci red 
             * dobijen kao rezultat izvrsavanja upita.
             * Ako je false, to znaci da korisnicko ime nije zauzeto,
             * tj. ne postoji red sa tim username-om u bazi */
            if (!resultSet.next()) {
                isAvailable = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAvailable;
    }
	
	public static boolean usernameMatchesPassword(String username, String password) {
		String hashedPassword = hashPassword(password);
		
		UserDAO pom = UserDAO.getUser(username);
		if(!hashedPassword.equals(pom.getHashedPassword())) {
			return false;
		}
		return true;
	}
	
	public static String hashPassword(String password) {
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
			
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
}
