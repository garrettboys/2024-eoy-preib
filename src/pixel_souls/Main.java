package pixel_souls;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JFrame;

public class Main extends JFrame{
	private static final long serialVersionUID = 8162784762091143676L; // auto gen by Eclipse, unnessecary but warning is annoying
	private static final int WIDTH =1312;
	private static final int HEIGHT=704;
	
	public Main () {
		super("Pixel Souls");
		setSize(WIDTH, HEIGHT);
		Game play = new Game();
		((Component) play).setFocusable(true);
		
		setBackground(Color.BLACK);
		
		
		getContentPane().add(play);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Main run = new Main();
	}


}
