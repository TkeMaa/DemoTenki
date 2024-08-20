package entity;

import java.awt.Rectangle;

import main.GamePanel;

public class EnemyPlayer extends Entity {

//	public boolean moved_up = false;
//	public boolean moved_down = false;
//	public boolean moved_left = false;
//	public boolean moved_right = false;
	
	public final int solidAreaDefaultX = 12;
	public final int solidAreaDefaultY = 12;
	
	public EnemyProjectile enemyProjectile;
	
	public EnemyPlayer(GamePanel gp) {
		super(gp);
		
		// Oblast kolizije
		solidArea = new Rectangle();
		
		solidArea.x = 12;
		solidArea.y = 12;
		solidArea.width = 26;
		solidArea.height = 26;
		
		enemyProjectile = new EnemyProjectile(gp);
		
		speed = 3;
		
		getPlayerImages();
	}
	
	private void getPlayerImages() {
		up1 = setup("/player/crveni_gore1");
		down1 = setup("/player/crveni_dole1");
		left1 = setup("/player/crveni_levo1");
		right1 = setup("/player/crveni_desno1");
		
		up2 = setup("/player/crveni_gore2");
		down2 = setup("/player/crveni_dole2");
		left2 = setup("/player/crveni_levo2");
		right2 = setup("/player/crveni_desno2");
	}
	
//	public void update() {
//		if (moved_up) {
//			worldY -= speed;
//		}
//		if (moved_down) {
//			worldY += speed;
//		}
//		if (moved_left) {
//			worldX -= speed;
//		}
//		if (moved_right) {
//			worldX += speed;
//		}
//	}
	
	
}
