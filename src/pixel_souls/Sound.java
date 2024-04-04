package pixel_souls;
import javax.sound.sampled.*;
import java.io.*;

public class Sound
{
	
	public void playmusic(String musicfile) {
		File soundFile = new File(musicfile);
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream= AudioSystem.getAudioInputStream(soundFile);
			clip.open(inputStream);
			clip.loop(0);
			clip.start();
		}
	catch(Exception e)
		{
		System.out.println(e);
		}
	}
}