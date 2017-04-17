package components.items;

import components.map.rooms.Room;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class MapItem
{
	protected Room room;

	protected int x;
	protected int y;

	protected int width = 16;
	protected int height = 16;

	protected BufferedImage bufferedImage;

	public abstract void update();

	public void draw(Graphics2D g2d)
	{
		g2d.drawImage(bufferedImage, x - width / 2,
				y - height / 2, width, height, null);
	}

	public Rectangle getRectangle()
	{
		return new Rectangle(x, y, width, height);
	}
}