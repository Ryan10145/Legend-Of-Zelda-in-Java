package components.entity.enemies;

import components.map.rooms.Room;
import utility.Animation;
import utility.Images;

import java.awt.*;

public class Peahat extends Enemy
{
	private int direction;

	private Animation animation;

	private double speed;
	private boolean speedingUp;
	private int timer;

	public Peahat(int x, int y, int direction, Room room)
	{
		this.x = x;
		this.y = y;

		width = 16;
		height = 16;

		this.direction = direction;

		this.room = room;

		speed = 10;
		speedingUp = false;
		timer = 0;

		animation = new Animation(10, true, Images.Enemies.Peahat.PEAHAT_1, Images.Enemies.Peahat.PEAHAT_2);

		health = 2;
		damage = 4;
	}

	public void update()
	{
		if(getStunTimer() == 0)
		{
			if(Math.random() * 250 > 248)
			{
				int newDirection = (int) (Math.random() * 8);
				while(newDirection == direction)
				{
					newDirection = (int) (Math.random() * 8);
				}

				direction = newDirection;
			}

			double angle = direction * Math.PI / 4;

			velX = Math.cos(angle) * speed / 10;
			velY = Math.sin(angle) * speed / 10;

			x += velX;
			y += velY;

			Rectangle screen = new Rectangle(room.getMapWidth(), room.getMapHeight());
			if(!screen.intersects(getRectangle()))
			{
				direction = ((direction + 4) + (int) (Math.random() * 3 - 1.5)) % 8;
			}

			animation.setSpeed((int) (15 - speed));
			if(animation.getSpeed() == 14) animation.setSpeed(120);

			animation.update();
		}
		else setStunTimer(getStunTimer() - 1);

		super.update();

		speed = (Math.cos(timer / 90) * 5) + 5;
		timer++;
	}

	public void draw(Graphics2D g2d)
	{
		drawX = (int) Math.round(x);
		drawY = (int) Math.round(y);

		if(!(invincibilityFrames > 0 && invincibilityFrames % 3 == 0))
		{
			animation.draw(g2d, drawX, drawY, width, height);
		}
	}
}
