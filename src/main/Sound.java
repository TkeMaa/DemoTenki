package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

	
	Clip clip;
	URL soundURL[] = new URL[6];
	public static final int battleMusic = 0;
	public static final int mainMusic = 1;
	public static final int shoot = 2;
	public static final int chipWall = 3;
	public static final int reload = 4;
	public static final int cursor = 5;
	
	public Sound() {
		
		soundURL[0] = getClass().getResource("/sound/in_game.wav");
		soundURL[1] = getClass().getResource("/sound/main_menu.wav");
		soundURL[2] = getClass().getResource("/sound/shoot.wav");
		soundURL[3] = getClass().getResource("/sound/chip_wall.wav");
		soundURL[4] = getClass().getResource("/sound/reload.wav");
		soundURL[5] = getClass().getResource("/sound/cursor.wav");
	}
	
	public void setFile(int i, float decibelOffset) {
		
		try {
			
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			
			clip.open(ais);
			
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(decibelOffset);
			
		} catch (Exception e) {
			
		}
	}
	
	public void play() {
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void stop() {
		clip.stop();
	}

}
