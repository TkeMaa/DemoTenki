package main;

import entity.Entity;
import entity.Player;
import entity.Projectile;

public class CollisionChecker {

	GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		
		this.gp = gp;
	}
	
	// Proveri koliziju sa enemy igracem
	public void checkEnemyCollision(Entity entity) {
		
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		
		gp.enemy.solidArea.x= gp.enemy.worldX + gp.enemy.solidArea.x;
		gp.enemy.solidArea.y = gp.enemy.worldY + gp.enemy.solidArea.y;
		
		switch(entity.direction) {
		case "up":
			entity.solidArea.y -= entity.speed;
			if (entity.solidArea.intersects(gp.enemy.solidArea)) {
				if (entity instanceof Player) {
					((Player) entity).collisionOn = true;
				}
				if (entity instanceof Projectile) {
					((Projectile) entity).hitEnemy = true;
				}
			}
			break;
		case "down": 
			entity.solidArea.y += entity.speed;
			if (entity.solidArea.intersects(gp.enemy.solidArea)) {
				if (entity instanceof Player) {
					((Player) entity).collisionOn = true;
				}
				if (entity instanceof Projectile) {
					((Projectile) entity).hitEnemy = true;
				}
			}
			break;
		case "left": 
			entity.solidArea.x -= entity.speed;
			if (entity.solidArea.intersects(gp.enemy.solidArea)) {
				if (entity instanceof Player) {
					((Player) entity).collisionOn = true;
				}
				if (entity instanceof Projectile) {
					((Projectile) entity).hitEnemy = true;
				}
			}
			break;
		case "right": 
			entity.solidArea.x += entity.speed;
			if (entity.solidArea.intersects(gp.enemy.solidArea)) {
				if (entity instanceof Player) {
					((Player) entity).collisionOn = true;
				}
				if (entity instanceof Projectile) {
					((Projectile) entity).hitEnemy = true;
				}
			}
			break;
		}
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gp.enemy.solidArea.x = gp.enemy.solidAreaDefaultX;
		gp.enemy.solidArea.y = gp.enemy.solidAreaDefaultY;
	}
	
	public int[] checkTile(Entity entity) {
		
		// SOLID AREA GRANICE ZA ENTITY:
		// LEVO
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		// DESNO
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		// GORE
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		// DOLE
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		// RED/KOLONA U KOJOJ SE GRANICE NALAZE
		int entityLeftCol = entityLeftWorldX/GamePanel.tileSize;
		int entityRightCol = entityRightWorldX/GamePanel.tileSize;
		int entityTopRow = entityTopWorldY/GamePanel.tileSize;
		int entityBottomRow = entityBottomWorldY/GamePanel.tileSize;
		
		int tileNum1 = 0, tileNum2 = 0;
		
		// AKO JE RED/KOLONA PLOCICE KADA BI SE KRETALI U ODREDJENOM SMERU "SOLID", KOLIZIJA JE "ON"
		switch(entity.direction) {
		case "up":
			// GORNJI RED KADA BISMO SE POMERILI ZA JEDNU VREDNOST "SPEED" KA GORE
			entityTopRow = (entityTopWorldY - entity.speed)/GamePanel.tileSize;
			// PLOCICE KOJE PRIPADAJU GORNJEM REDU I LEVOJ I DESNOJ GRANICI SOLID AREA
			tileNum1 = gp.tileM.mapTileNums[entityTopRow][entityLeftCol];
			tileNum2 = gp.tileM.mapTileNums[entityTopRow][entityRightCol];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				return new int[] {entityTopRow, entityLeftCol};
			}
			break;
		case "down": 
			// DONJI RED KADA BISMO SE POMERILI ZA JEDNU VREDNOST "SPEED" KA DOLE
			entityBottomRow = (entityBottomWorldY + entity.speed)/GamePanel.tileSize;
			// PLOCICE KOJE PRIPADAJU DONJEM REDU I LEVOJ I DESNOJ GRANICI SOLID AREA
			tileNum1 = gp.tileM.mapTileNums[entityBottomRow][entityLeftCol];
			tileNum2 = gp.tileM.mapTileNums[entityBottomRow][entityRightCol];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				return new int[] {entityBottomRow, entityLeftCol};
			}
			break;
		case "left": 
			// LEVA KOLONA KADA BISMO SE POMERILI ZA JEDNU VREDNOST "SPEED" KA LEVO
			entityLeftCol = (entityLeftWorldX - entity.speed)/GamePanel.tileSize;
			// PLOCICE KOJE PRIPADAJU LEVOJ KOLONI I GORNJOJ I DONJOJ GRANICI SOLID AREA
			tileNum1 = gp.tileM.mapTileNums[entityTopRow][entityLeftCol];
			tileNum2 = gp.tileM.mapTileNums[entityBottomRow][entityLeftCol];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				return new int[] {entityTopRow , entityLeftCol};
			}
			break;
		case "right": 
			// DESNA KOLONA KADA BISMO SE POMERILI ZA JEDNU VREDNOST "SPEED" KA DESNO
			entityRightCol = (entityRightWorldX + entity.speed)/GamePanel.tileSize;
			// PLOCICE KOJE PRIPADAJU DESNOJ KOLONI I GORNJOJ I DONJOJ GRANICI SOLID AREA
			tileNum1 = gp.tileM.mapTileNums[entityTopRow][entityRightCol];
			tileNum2 = gp.tileM.mapTileNums[entityBottomRow][entityRightCol];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				return new int[] {entityTopRow, entityRightCol};
			}
			break;
		}
		return new int[] {tileNum1, tileNum2};
	}
	
}
