package pixel_souls;

import javax.sound.sampled.*;
import java.io.*;

public class SoundPlayer {

    private Clip musicClip; 

    public void playMusic(String musicFile) {
        if (musicClip != null && musicClip.isOpen()) {
            musicClip.stop(); 
            musicClip.close(); 
        }
        musicClip = loadClip(musicFile);
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); 
            musicClip.start();
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
        }
    }

    public void playSoundEffect(String soundFile) {
        Clip soundClip = loadClip(soundFile);
        if (soundClip != null) {
            soundClip.start();  
        }
    }

    private Clip loadClip(String filePath) {
        File soundFile = new File(filePath);
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            return clip;
        } catch (Exception e) {
            System.out.println("Could not load sound file: " + filePath);
            System.out.println("Error: " + e);
            return null;
        }
    }
}
