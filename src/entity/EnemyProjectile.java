package entity;

import main.GamePanel;

public class EnemyProjectile extends Projectile {

	public EnemyProjectile(GamePanel gp) {
		super(gp);
	}

	public void set(int worldX, int worldY, String direction, boolean alive, Entity user) {
		this.worldX = worldX;
		this.worldY = worldY;
		this.direction = direction;
		this.alive = alive;
		this.user = user;
		this.life = maxLife;
	}
	
	public void update() {
		
		switch (direction) {
			case "up": worldY -= speed; break;
			case "down": worldY += speed; break;
			case "left": worldX -= speed; break;
			case "right": worldX += speed; break;		
		}

		life--;
		if (life < 0) {
			alive = false;
		}
		
		spriteNum++;
		if (spriteNum > 2) {
			spriteNum = 1;
		}
		
	}
	
}
