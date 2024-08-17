package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import base.InvitePlayerForm;
import base.LoginForm;
import base.SignupForm;
import base.UserDAO;
import entity.Entity;
import entity.Player;
import entity.Projectile;
import networking.GameClient;
import networking.GameServer;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	// SCREEN SETTINGS
	private final static int originalTileSize = 16;
	private final static int scale = 3; // Default velicina za povecavanje objekata na ekranu
	
	public static final int tileSize = originalTileSize * scale; // 48x48 - velicina polja
	public final int maxScreenCols = 16; // Maks broj kolona na ekranu
	public final int maxScreenRows = 12; // Maks broj redova na ekranu
	public final int screenWidth = maxScreenCols * tileSize; // Sirina ekrana - 768 
	public final int screenHeight = maxScreenRows * tileSize; // Visina ekrana - 576
	
	// FORME
	public SignupForm signupForm = null;
	public LoginForm loginForm = null;
	public InvitePlayerForm invitePlayerForm = null;
	
	// USER
	public UserDAO user = null;
	
	// WORLD SETTINGS
	public final int maxWorldCols = 35; // Maks broj kolona mape
	public final int maxWorldRows = 35; // Maks broj redova mape
	
	// FRAMERS PER SECOND
	final int FPS = 60;
	
	// KEYHANDLER
	public KeyHandler keyH = new KeyHandler(this);
	
	// GAME THREAD
	public Thread gameThread;
	
	// PLAYER
	public Player player = new Player(this, keyH);
	
	// ENTITY LIST, PROJECTILE LIST
	public ArrayList<Projectile> projectileList = new ArrayList<>();
	public ArrayList<Entity> entityList = new ArrayList<>();
	
	// TILE MANAGER
	public TileManager tileM = new TileManager(this, "/maps/mapa35x35.txt");
	
	// COLLISION DETECTION
	public CollisionChecker cChecker = new CollisionChecker(this);
	
	// SOUND 
	public Sound music = new Sound();
	public Sound sound = new Sound();
	
	// GAME STATE
	public int gameState;
	public static final int titleState = 0;
	public static final int inGameState = 1;
	public static final int playState = 2;
	
	// UI
	public UI ui = new UI(this);
	
	// Client i Server - Server moze biti pokrenut samo na racunaru cija je javna ip adresa: sendAddress
	public GameClient socketClient;
	public GameServer socketServer;
	
	// Adresa i port serverskog rutera:
	private static int serverPort = 12345; 
	private static String serverAddress = "79.175.76.229";
	
	// URL za proveru javne ip adrese
	private static final String checkPublicIpString = "http://checkip.amazonaws.com/";
	
	// CONSTRUCTOR
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true); // Poboljsava performanse
		this.addKeyListener(keyH);
		this.setFocusable(true); // Ovo mora da bi moglo da primi input sa tastature
	}

	public static String getPublicIp(){
		URL url = null;
		String ip;
		try {
			url = new URL(checkPublicIpString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
		    ip = br.readLine();
		    System.out.println("Vasa javna ipv4 adresa: " + ip.trim());
			return ip;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Server moze biti pokrenut samo na racunaru cija je javna ip adresa: sendAddress
	private boolean serverRunnable() {
		String ip = getPublicIp();
		if (ip == null) {
			return false;
		}
		return ip.equals(serverAddress);
	}
	
	public void startGameThread() {
				
		gameThread = new Thread(this);
		gameThread.start();	
		gameState = titleState;
		playMusic(Sound.mainMusic);	
		
		if (serverRunnable()) {
			if (JOptionPane.showConfirmDialog(this, "Da li zelis da pokrenes server?") == 0) {
				socketServer = new GameServer(this);
	            socketServer.start();
	            System.out.println("Server uspesno pokrenut");
			}
		} else {
			System.out.println("Niste u mogucnosti da pokrenete server na vasem racunaru.");
		}
		
		socketClient = new GameClient(this, serverAddress, serverPort);
		socketClient.start();
		socketClient.sendData(GameServer.testPacket,"ping");
	}
	
	// Najprecizniji nacin
	public void run() {
		
		final double drawInterval = 1000000000/FPS;

		long lastTime = System.nanoTime();
		long currentTime;

		double delta = 0;
		long timer = 0;
		int drawCount = 0;
		
		while (gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			
			timer += currentTime - lastTime;
			
			lastTime = currentTime;
			
			if (delta >= 1) {
				
				update();
				
				repaint();
				
				delta--;
				drawCount++;
			}
			
			if (timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}			
		}		
	}
	
//	@Override
//	public void run() {
//	
//		long timer = 0;
//		int drawCount = 0;
//		long lastTime = System.nanoTime();
//		long currentTime;
//		
//		// 1 000 000 000 nanosekundi = 1 sekund
//		final double drawInterval = 1000000000/FPS; // Vreme izmedju 1 osvezavanja ekrana: 0.016 sek
//		double nextDrawTime = System.nanoTime() + drawInterval;
//		
//		while(gameThread != null) {
//			
//			currentTime = System.nanoTime();
//			timer += (currentTime - lastTime);
//			lastTime = currentTime;
//			
//			update();
//			repaint(); // Pozivamo metodu paintComponent
//			drawCount++;
//
//			try {
//				
//				double remainingTime = nextDrawTime - System.nanoTime();
//				// Pretvaramo u milisekunde:
//				remainingTime = remainingTime/1000000; 
//				
//				if(remainingTime < 0) {
//					remainingTime = 0;
//				}
//				
//				// Prikazi FPS
//				if (timer >= 1000000000) {
//					System.out.println("FPS: " + drawCount);
//					drawCount = 0;
//					timer = 0;
//				}
//				
//				Thread.sleep((long) remainingTime);
//				
//				nextDrawTime += drawInterval;
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//	}
	
//	@Override
//	public void run() {
//		
//		ScheduledExecutorService gameExecutor = Executors.newScheduledThreadPool(1);
//	    ScheduledExecutorService fpsExecutor = Executors.newScheduledThreadPool(1);
//	    AtomicInteger drawCount = new AtomicInteger(0);
//	    
//        final long drawInterval = 1000000000 / 60; // oko 60 FPS
//        
//        gameExecutor.scheduleAtFixedRate(() -> {
//            update();
//            repaint();
//            drawCount.incrementAndGet();
//        }, 0, drawInterval / 1000000, TimeUnit.MILLISECONDS);
//
//        fpsExecutor.scheduleAtFixedRate(() -> {
//            System.out.println("FPS: " + drawCount);
//            drawCount.set(0);
//        }, 1, 1, TimeUnit.SECONDS);
//		
//	}
	
	public void update() {
		
		switch (gameState) {
		case titleState:
			break;
		case playState:
			//UserDAO.getOnlineUsers();
			break;
		case inGameState:
			// PLAYER UPDATE
			player.update();
			// PROJECTILE UPDATE
			projectileUpdate();
			break;
		default:
			break;
		}
	}
	
	public void projectileUpdate() {
		for (int i = 0; i < projectileList.size(); i++) {
			if (projectileList.get(i) != null) {
				if (projectileList.get(i).alive) {
					projectileList.get(i).update();
				}
				if (!projectileList.get(i).alive) {
					projectileList.remove(i);
				}
			}
		}
	}
	
	// Ugradjena metoda u javi
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);		
		Graphics2D g2 = (Graphics2D)g;

		if (gameState == inGameState) {	
			// Renderujemo plocice:
			tileM.draw(g2);	
			// Dodajemo sve entitete u entityList:
			// IGRAC
			entityList.add(player);
			// PROJEKTILI
			for (int i = 0; i < projectileList.size();i++) {
				if (projectileList.get(i) != null) {
					entityList.add(projectileList.get(i));
				}
			}
//			// Sortiraj redosled entita
//			Collections.sort(entityList, new Comparator<Entity>() {
//
//				@Override
//				public int compare(Entity e1, Entity e2) {
//
//					int result = Integer.compare(e1.worldY, e2.worldY);
//					return result;
//				}
//				
//			});
			// Renderujemo entitete:
			for (int i = 0; i < entityList.size(); i++) {
				entityList.get(i).draw(g2);
//				// Proveri koordinate igraca
//				if (entityList.get(i) instanceof Player) {
//					Player p = (Player)entityList.get(i);
//					System.out.println("world x: " + p.worldX);
//					System.out.println("world y: " + p.worldY);
//					System.out.println("screen x: " + p.screenX);
//					System.out.println("screen y: " + p.screenY);
//					System.out.println("==================================================================");
//				}
			}
			// Praznimo entityList u svakom pozivu metode repaint();
			entityList.clear();
		} 
		// Renderujemo UI
		ui.draw(g2);
		g2.dispose();
	}
	
	public void playMusic(int i) {
		music.setFile(i, -18.0f);
		music.play();
		music.loop();
	}
	
	public void stopMusic() {
		music.stop();
	}
	
	public void playSE(int i, float decibelOffset) {
		sound.setFile(i, decibelOffset);
		sound.play();
	}
}
