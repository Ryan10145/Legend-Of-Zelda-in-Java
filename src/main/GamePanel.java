package main;

import state.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
	public static final int WIDTH = 256;
	public static final int HEIGHT = 216;
	private static final int SCALE = 2;

	private Thread updateThread;
	private boolean running;

	private BufferedImage image;
	private Graphics2D g2d;

	private StateManager stateManager;

	public static final int FPS = 60;

	GamePanel()
	{
		super();
		this.setFocusable(true);
		this.requestFocus();
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
	}

	public void addNotify()
	{
		super.addNotify();

		if(updateThread == null)
		{
			updateThread = new Thread(this);
			this.addKeyListener(this);
			updateThread.start();
		}
	}

	private void draw()
	{
		stateManager.draw(g2d);
	}

	private void drawToScreen()
	{
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.dispose();
	}

	private void init()
	{
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g2d = (Graphics2D) image.getGraphics();

		try
		{
			g2d.setFont(Font.createFont(Font.TRUETYPE_FONT,
					this.getClass().getResourceAsStream("/fonts/Legend of Zelda.ttf"))
					.deriveFont(7f));
		}
		catch(FontFormatException | IOException e)
		{
			e.printStackTrace();
		}

		running = true;
		stateManager = new StateManager();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e != null) stateManager.keyPressed(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e != null) stateManager.keyReleased(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void run()
	{
		init();
		long start;
		long elapsed;
		long wait;
		while(running)
		{
			start = System.nanoTime();
			update();
			draw();
			drawToScreen();

			long targetTime = 1000 / FPS;
			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;
			if(wait < 0) wait = 5;
			try
			{
				Thread.sleep(wait);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void update()
	{
		stateManager.update();
	}
}
