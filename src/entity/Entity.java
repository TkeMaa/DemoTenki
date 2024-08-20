package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class Entity{
	
	GamePanel gp;
	
	// Pozicija na mapi
	public int worldX, worldY;
	
	// Brzina kretanja
	public int speed;	

	// Sprite-ovi i smer
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
	public BufferedImage image1, image2;
	
	// Brojac za animaciju
	public int spriteNum = 1;
	
	// Status entiteta
	public String direction = "down";
	public String name;
	public int maxLife;
	public int life;
	public int hp;
	public int maxHp;
	public boolean alive = true;
	
	public boolean dying = false;
	int dyingCounter = 0;
		
	// Podesavanja kolizije
	public int solidAreaDefaultX;
	public int solidAreaDefaultY;
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
	public boolean collisionOn = false;
	public boolean collision = false;	
	
	// Projektil
	public Projectile projectile;
	public int attack;

	// Konstruktor
	public Entity(GamePanel gp) {
		// Zivoti i hp
		maxLife = 3;
		life = maxLife;
		maxHp = 48;
		hp = maxHp;
		
		this.gp = gp;
	}
	
	public void update() {
		
	}
	
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
	
		if (worldX + GamePanel.tileSize > gp.player.worldX - gp.player.screenX &&
			worldX - GamePanel.tileSize < gp.player.worldX + gp.player.screenX &&
			worldY + GamePanel.tileSize > gp.player.worldY - gp.player.screenY &&
			worldY - GamePanel.tileSize < gp.player.worldY + gp.player.screenY) {
				
			switch (direction) {
				case "up": 
					if (spriteNum == 1) {
						image = up1;
					} 
					if (spriteNum == 2) {
						image = up2;
					}
					break;
				case "down": 
					if (spriteNum == 1) {
						image = down1;
					} 
					if (spriteNum == 2) {
						image = down2;
					}
					break;
				case "left": 
					if (spriteNum == 1) {
						image = left1;
					} 
					if (spriteNum == 2) {
						image = left2;
					}
					break;
				case "right": 
					if (spriteNum == 1) {
						image = right1;
					} 
					if (spriteNum == 2) {
						image = right2;
					}
					break;
			}	
			
			if(dying == true) {
				dyingAnimation(g2);
			}
			
			if (this instanceof EnemyPlayer) {
				
				double oneScale = (double)GamePanel.tileSize/maxHp;
				double hpBarValue = oneScale*hp;
				
				g2.setColor(new Color(35, 35, 35));
				g2.fillRect(screenX - 1, screenY - 16, GamePanel.tileSize + 2, 12);;
				g2.setColor(new Color(255, 0, 30));
				g2.fillRect(screenX, screenY - 15, (int)hpBarValue, 10);
			}
			
			g2.drawImage(image, screenX, screenY, GamePanel.tileSize, GamePanel.tileSize, null);
		}
	}
	
	public void dyingAnimation(Graphics2D g2) {
		
		dyingCounter++;
		
		if(dyingCounter <= 5) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
		}
		if(dyingCounter > 5 && dyingCounter <= 10) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
		if(dyingCounter <= 5) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
		}
		if(dyingCounter <= 5) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
		if(dyingCounter <= 5) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
		}
	}
	
	public BufferedImage setup(String imagePath) {
		
		BufferedImage image= null;
		
		try {
			
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
			image = UtilityTool.scaleImage(image, GamePanel.tileSize, GamePanel.tileSize);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
}