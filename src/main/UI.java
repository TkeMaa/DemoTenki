package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import entity.Entity;
import object.OBJ_Heart;

public class UI {
	
	GamePanel gp;
	
	Font font;
	Graphics2D g2;
	
	// Title screen slike
	BufferedImage plavi_desno1;
	BufferedImage crveni_levo1;
	
	// HUD srca
	Entity heart;
	BufferedImage heart_full, heart_blank;
	
	// Opcija
	public int commandNum = 0;
	
	// Konstruktor
	public UI(GamePanel gp) {
		
		this.gp = gp;
		
		getFont();
		getTitleScreenImages();
			
		// HUD srca
		heart = new OBJ_Heart(gp);

		heart_full = heart.image1;
		heart_blank = heart.image2;
	}
	
	public void getFont(){
		
		InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
		try {
			
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getTitleScreenImages() {
		try {
			
			plavi_desno1 = ImageIO.read(getClass().getResourceAsStream("/player/plavi_desno1.png"));
			crveni_levo1 = ImageIO.read(getClass().getResourceAsStream("/player/crveni_levo1.png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}

	public void drawReadyToShoot() {
		
		int x = GamePanel.tileSize;
		int y = gp.screenHeight - GamePanel.tileSize;
		
		g2.setColor(Color.black);
		g2.setFont(font);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
		
		g2.drawString("Ready to fire!", x, y);
		
		g2.setColor(Color.white);
		g2.setFont(font);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
		
		g2.drawString("Ready to fire!", x-2, y-2);
	}
	
	public void drawPlayerLife() {
		
		int x = GamePanel.tileSize/2;
		int y = GamePanel.tileSize/2;
		int life = gp.player.life;
		
		for (int i = 0; i < gp.player.maxLife; i++) {
			if (life > 0) {
				g2.drawImage(heart_full, x, y, GamePanel.tileSize, GamePanel.tileSize, null);
				x+=GamePanel.tileSize;
				life--;
			} else {
				g2.drawImage(heart_blank, x, y, GamePanel.tileSize, GamePanel.tileSize, null);
				x+=GamePanel.tileSize;
			}
		}
	}
	
	public void drawWinScreen() {
		
	}
	
	public void drawLoseScreen() {
		
	}
	
	public void drawPlayScreen() {
		
		// BACKGROUND
		g2.setColor(new Color(70, 120, 105));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		g2.setFont(font);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 35F));
		
		// ONLINE
		g2.setColor(Color.black);
		g2.fillOval(3, 8, 24, 24);
		g2.setColor(Color.green);
		g2.fillOval(4, 9, 22, 22);
		
		// USERNAME SHADOW
		String username = gp.user.getUsername();
		g2.setColor(Color.black);
		g2.drawString(username, 36, 32);
		// DISPLAY USERNAME
		g2.setColor(Color.white);
		g2.drawString(username, 34, 30);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 144F));
		
		String text = "Tenki";
		int x = getXforCenteredText(text) - 8;
		int y = GamePanel.tileSize*3;
		
		// TITLE SHADOW
		g2.setColor(Color.black);
		g2.drawString(text, x+5, y+5);
		
		// TITLE
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		// IMAGE
		g2.drawImage(plavi_desno1, gp.screenWidth/2 - 144, GamePanel.tileSize*4, GamePanel.tileSize*2, GamePanel.tileSize*2, null);
		g2.drawImage(crveni_levo1, gp.screenWidth/2 + 48, GamePanel.tileSize*4, GamePanel.tileSize*2, GamePanel.tileSize*2, null);
		
		// MENU
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
		
		// PLAY OFFLINE
		text = "SINGLEPLAYER";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize*4 + 72;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 0) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
		
		// INVITE A PLAYER
		text = "INVITE A PLAYER INTO BATTLE";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 1) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
		
		// EXIT
		text = "EXIT";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 2) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
	}
	
	public void drawTitleScreen() {
		
		// BACKGROUND
		g2.setColor(new Color(70, 120, 105));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		g2.setFont(font);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 144F));
		String text = "Tenki";
		int x = getXforCenteredText(text) - 8;
		int y = GamePanel.tileSize*3;
		
		// TITLE SHADOW
		g2.setColor(Color.black);
		g2.drawString(text, x+5, y+5);
		
		// TITLE
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		// IMAGE
		g2.drawImage(plavi_desno1, gp.screenWidth/2 - 144, GamePanel.tileSize*4, GamePanel.tileSize*2, GamePanel.tileSize*2, null);
		g2.drawImage(crveni_levo1, gp.screenWidth/2 + 48, GamePanel.tileSize*4, GamePanel.tileSize*2, GamePanel.tileSize*2, null);
		
		// MENU
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
		
		// LOG IN
		text = "LOG IN";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize*4 + 72;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 0) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
		
		// SIGN UP
		text = "SIGN UP";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 1) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
		
		// EXIT
		text = "EXIT";
		x = getXforCenteredText(text);
		y += GamePanel.tileSize;
		
		g2.setColor(Color.black);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		if (commandNum == 2) {
			
			g2.setColor(Color.black);
			g2.drawString(">", x - GamePanel.tileSize + 2, y + 2);

			g2.setColor(Color.white);
			g2.drawString(">", x - GamePanel.tileSize, y);
		}
	}
	
	public int getXforCenteredText(String text) {
		
		int len = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.screenWidth/2 - len/2;
		return x;
	}
	
	public void draw(Graphics2D g2) {
		
		this.g2 = g2;
		g2.setFont(font);
		g2.setColor(Color.white);

		// PLAY STATE
		if (gp.gameState == GamePanel.playState) {
			drawPlayScreen();
		}
		// TITLE STATE
		if (gp.gameState == GamePanel.titleState) {
			drawTitleScreen();
		}
		// IN GAME STATE
		if (gp.gameState == GamePanel.inGameState) {
			drawPlayerLife();
			if (gp.player.projectile.readyToFire && !gp.player.projectile.alive) {
				drawReadyToShoot();	
			}
		}
//		// WIN STATE
//		if (gp.gameState == GamePanel.winState) {
//			drawWinScreen();
//		}
//		// LOSE STATE
//		if (gp.gameState == GamePanel.loseState) {
//			drawLoseScreen();
//		}
	}	
}