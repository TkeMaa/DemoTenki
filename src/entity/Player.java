package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;
import main.Sound;
import tile.TileManager;

public class Player extends Entity{

	KeyHandler keyH;

	public final int screenX;
	public final int screenY;
	
	public Player(GamePanel gp, KeyHandler keyH) {
		
		super(gp);

		this.keyH = keyH;
		
		// Centriramo igraca u prozoru - fiksne koordinate igraca na ekranu
		screenX = gp.screenWidth/2 - (GamePanel.tileSize/2);
		screenY = gp.screenHeight/2 - (GamePanel.tileSize/2);
		
		// Oblast kolizije
		solidArea = new Rectangle();
		solidArea.x = 12;
		solidArea.y = 12;
		solidArea.width = 26;
		solidArea.height = 26;
		
		getPlayerImage();
		setDefaultValues();
	}	
	
	public void setDefaultValues() {
		
		// Postavljamo spawn koordinate
		worldX = GamePanel.tileSize * 3;
		worldY = GamePanel.tileSize * 3;

		speed = 3;
		direction = "down";
		
		// Zivoti i hp
		maxLife = 3;
		life = maxLife;
		maxHp = 48;
		hp = maxHp;
		
		// Projektil
		projectile = new Projectile(gp);		
	}
	
	public void getPlayerImage() {
		
		up1 = setup("/player/plavi_gore1");
		down1 = setup("/player/plavi_dole1");
		left1 = setup("/player/plavi_levo1");
		right1 = setup("/player/plavi_desno1");
		
		up2 = setup("/player/plavi_gore2");
		down2 = setup("/player/plavi_dole2");
		left2 = setup("/player/plavi_levo2");
		right2 = setup("/player/plavi_desno2");
	}
	
public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		
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
		
		// HP bar
		if (this instanceof Player) {
			
			double oneScale = (double)GamePanel.tileSize/maxHp;
			double hpBarValue = oneScale*hp;
			
			g2.setColor(new Color(35, 35, 35));
			g2.fillRect(screenX - 1, screenY - 16, GamePanel.tileSize + 2, 12);;
			g2.setColor(new Color(255, 0, 30));
			g2.fillRect(screenX, screenY - 15, (int)hpBarValue, 10);
		}	
		
		g2.drawImage(image, screenX, screenY, null);
	}
	
	
	public void update() {	
		
		// OVAJ IF MORA KAKO SE IGRAC NE BI KRETAO SAM OD SEBE NAKON JEDNOG PRITISKA DUGMETA, KAO I NA SAMOM POCETKU IGRE,
		// SOBZIROM DA JE DEFAULT POZICIJA "DOLE".
		// IGRAC SE POMERA SAMO U KOLIKO SE KONTINUALNO PRITISKAJU DUGMICI
		if (keyH.downPressed || keyH.upPressed || keyH.leftPressed || keyH.rightPressed) {
			
			if(keyH.upPressed) {
				direction = "up";
			}
			else if(keyH.downPressed) {
				direction = "down";
			}
			else if(keyH.leftPressed) {
				direction = "left";
			}
			else if(keyH.rightPressed) {
				direction = "right";
			}
			// Animacija
			spriteNum++;
			if (spriteNum > 2) {
				spriteNum = 1;
			}
			
			// Proveravamo koliziju
			collisionOn = false;
			gp.cChecker.checkTile(this);
			
			// Ako nema kolizije u odredjenom smeru, igrac moze da se krece 
			if (!collisionOn) {
				switch (direction) {
				case "up":
					worldY -= speed;
					break;
				case "down":
					worldY += speed;
					break;
				case "left":
					worldX -= speed;
					break;
				case "right":
					worldX += speed;
					break;
				}
			}
		}
		
		// Projektil moze da se ispali samo ukoliko je prethodni nestao i ako je isteklo vreme punjenja
		if (gp.keyH.shootKeyPressed && !projectile.alive && projectile.readyToFire) {
			
			// Postavljamo default parametre za projektil 
			// U set metodi se ready parametar za projektil postavlja na false
			projectile.set(worldX, worldY, direction, true, this);
			
			gp.projectileList.add(projectile);
			
			gp.playSE(Sound.shoot, -10.0f);
		}
	}

	public void damageWall(int row, int col) {
		switch (gp.tileM.mapTileNums[row][col]) {
			case TileManager.wall: 
				gp.playSE(Sound.chipWall, 90.0f);
				gp.tileM.mapTileNums[row][col] = TileManager.wallDamaged1;
				break;
			case TileManager.wallDamaged1:
				gp.playSE(Sound.chipWall, 90.0f);
				gp.tileM.mapTileNums[row][col] = TileManager.wallDamaged2;
				break;
			case TileManager.wallDamaged2:
				gp.playSE(Sound.chipWall, 90.0f);
				gp.tileM.mapTileNums[row][col] = TileManager.sand;
				break;
		}
	}

	public void setDefaultCoordinates(int row, int col) {
		this.worldX = row * GamePanel.tileSize;
		this.worldY = col * GamePanel.tileSize;
	}
	
}
