package main;

import javax.swing.*;

class Game
{
	//Credit - Zephiel87 for tileset

	public static void main(String[] args)
	{
		new Game().start();
	}

	private void start()
	{
		setUpFrame();
	}

	private void setUpFrame()
	{
		JFrame window  = new JFrame("Legend of Zelda");
		window.setContentPane(new GamePanel());
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.pack();
		window.setLocationRelativeTo(null);
	}
}
