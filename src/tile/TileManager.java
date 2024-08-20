package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class TileManager {

	GamePanel gp;
	public Tile[] tile;
	public int mapTileNums[][];
	
	public static final int sand = 0;
	public static final int wall = 1;
	public static final int wallUnbreakable = 2;
	public static final int wallDamaged1 = 3;
	public static final int wallDamaged2 = 4;
	
	public TileManager(GamePanel gp, String filePath) {
				
		this.gp = gp;
		
		tile = new Tile[5];
		mapTileNums = new int[gp.maxWorldRows][gp.maxWorldCols];
		
		getTileImage();
		loadMap(filePath);
	}
	
	public void getTileImage() {

		setup(0, "sand", false);
		setup(2, "wall_unbreakable", true);
		setup(1, "wall", true);	
		setup(3, "wall_damaged1", true);
		setup(4, "wall_damaged2", true);
	}
	
	public void setup(int index, String imagePath, boolean collision) {

		try {
			
			tile[index] = new Tile();
			tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imagePath + ".png"));
			tile[index].image = UtilityTool.scaleImage(tile[index].image, GamePanel.tileSize, GamePanel.tileSize);
			tile[index].collision = collision;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadMap(String filePath) {
		
		try {		
			
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String pom;
			String[] nums;
			
			for (int i = 0; i < gp.maxWorldRows; i++) {				
				
				pom = br.readLine();	
				nums = pom.split(" ");
				
				for (int j = 0; j < gp.maxWorldCols; j++) {
					mapTileNums[i][j] = Integer.parseInt(nums[j]);
				}
			}
			
//			// Provera ucitane mape
//			System.out.println("Ucitana mapa:");
//			for (int i = 0; i < gp.maxScreenRows; i++) {
//				for (int j = 0; j < gp.maxScreenCols; j++) {
//					System.out.print(mapTileNums[i][j] + " ");
//				}
//				System.out.println();
//			}
			
			br.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Prikaz plocica na ekranu
	public void draw(Graphics2D g2) {
		
		int worldCol = 0;
		int worldRow = 0;
		int tileNum;
		
		while (worldCol < gp.maxWorldCols && worldRow < gp.maxWorldRows) {
			
			tileNum = mapTileNums[worldRow][worldCol];
			
			int worldX = worldCol * GamePanel.tileSize;
			int worldY = worldRow * GamePanel.tileSize;
			// Pozicija plocice na EKRANU - offsetujemo za poziciju igraca na mapi i koordinate igraca na ekranu  
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			// Renderuj mapu samo u okviru ekrana - poboljsava performanse
			if (worldX + GamePanel.tileSize > gp.player.worldX - gp.player.screenX &&
				worldX - GamePanel.tileSize < gp.player.worldX + gp.player.screenX &&
				worldY + GamePanel.tileSize > gp.player.worldY - gp.player.screenY &&
				worldY - GamePanel.tileSize < gp.player.worldY + gp.player.screenY) {
				g2.drawImage(tile[tileNum].image, screenX, screenY, GamePanel.tileSize, GamePanel.tileSize, null);
			}
			
			worldCol++;
			
			if (worldCol == gp.maxWorldCols) {
				
				worldCol = 0;
				worldRow++;
			}
		}
	}
}
