package entity;

import main.GamePanel;
import reference.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

//TODO Add sword sprites
public class Link
{
	private int x;
	private int y;

	private int velX;
	private int velY;

	private int moveSpeed;

	private int width;
	private int height;

	private boolean left;
	private boolean up;
	private boolean right;
	private boolean down;
	private boolean attack;

	private int direction;
	private String state;

	private Sword sword;
	private int timer;

	private Animation walkUp;
	private Animation walkRight;
	private Animation walkDown;
	private Animation walkLeft;

	private BufferedImage[] swordAttack;

	public Link()
	{
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2;

		moveSpeed = 2;

		width = 16;
		height = 16;

		timer = 0;

		walkUp = new Animation(4, Images.LINK_UP, Images.LINK_UP_2);
		walkRight = new Animation(4, Images.LINK_RIGHT, Images.LINK_RIGHT_2);
		walkDown = new Animation(4, Images.LINK_DOWN, Images.LINK_DOWN_2);
		walkLeft = new Animation(4, Images.LINK_LEFT, Images.LINK_LEFT_2);

		swordAttack = new BufferedImage[] {Images.LINK_ATTACK_SWORD_UP, Images.LINK_ATTACK_SWORD_RIGHT,
				                           Images.LINK_ATTACK_SWORD_DOWN, Images.LINK_ATTACK_SWORD_LEFT};

		state = "IDLE";
	}

	public void update()
	{
		switch(state)
		{
		case "IDLE":
			velX = 0;
			velY = 0;

			checkFreeMovement();
			break;
		case "UP":
			velX = 0;
			velY = -moveSpeed;
			direction = 0;

			walkUp.runAnimation();

			checkFreeMovement();
			break;
		case "DOWN":
			velX = 0;
			velY = moveSpeed;
			direction = 2;

			walkDown.runAnimation();

			checkFreeMovement();
			break;
		case "RIGHT":
			velX = moveSpeed;
			velY = 0;
			direction = 1;

			walkRight.runAnimation();

			checkFreeMovement();
			break;
		case "LEFT":
			velX = -moveSpeed;
			velY = 0;
			direction = 3;

			walkLeft.runAnimation();

			checkFreeMovement();
			break;
		case "ATTACK_SWORD_START":
			velX = 0;
			velY = 0;

			timer = 16;

			state = "ATTACK_SWORD";
			break;
		case "ATTACK_SWORD":
			if(timer == 9)
			{
				int drawX = x - width / 2;
				int drawY = y - height / 2;
				switch(direction)
				{
				case 0:
					sword = new Sword(drawX, drawY - 12, direction);
					break;
				case 1:
					sword = new Sword(drawX + 12, drawY, direction);
					break;
				case 2:
					sword = new Sword(drawX, drawY + 12, direction);
					break;
				case 3:
					sword = new Sword(drawX - 12, drawY, direction);
					break;
				default:
					break;
				}
			}
			else if(timer <= 2)
			{
				switch(direction)
				{
				case 0:
					sword.setVector(0, 4);
					break;
				case 1:
					sword.setVector(-4, 0);
					break;
				case 2:
					sword.setVector(0, -4);
					break;
				case 3:
					sword.setVector(4, 0);
					break;
				default:
					break;
				}
			}

			if(timer > 0) timer--;
			else
			{
				state = "IDLE";
				sword = null;
			}
			break;
		default:
			System.out.println(state);
			break;
		}

		if(sword != null) sword.update();

		x += velX;
		y += velY;
	}

	public void draw(Graphics2D g2d)
	{
		if(sword != null) sword.draw(g2d);

		switch(state)
		{
		case "IDLE":
			switch(direction)
			{
			case 0:
				walkUp.draw(g2d, x - width / 2, y - height / 2, width, height);
				break;
			case 1:
				walkRight.draw(g2d, x - width / 2, y - height / 2, width, height);
				break;
			case 2:
				walkDown.draw(g2d, x - width / 2, y - height / 2, width, height);
				break;
			case 3:
				walkLeft.draw(g2d, x - width / 2, y - height / 2, width, height);
				break;
			default:
				break;
			}
			break;
		case "UP":
			walkUp.draw(g2d, x - width / 2, y - height / 2, width, height);
			break;
		case "DOWN":
			walkDown.draw(g2d, x - width / 2, y - height / 2, width, height);
			break;
		case "RIGHT":
			walkRight.draw(g2d, x - width / 2, y - height / 2, width, height);
			break;
		case "LEFT":
			walkLeft.draw(g2d, x - width / 2, y - height / 2, width, height);
			break;
		case "ATTACK_SWORD_START":
			g2d.drawImage(swordAttack[direction], x - width / 2, y - height / 2, width, height, null);
			break;
		case "ATTACK_SWORD":
			g2d.drawImage(swordAttack[direction], x - width / 2, y - height / 2, width, height, null);
			break;
		default:
			g2d.setColor(Color.RED);
			g2d.drawRect(x - width / 2, y - height / 2, width, height);
			break;
		}
	}

	//Check for movement that you have when you can do anything, i.e. IDLE, or LEFT/RIGHT/UP/DOWN
	private void checkFreeMovement()
	{
		if(up) state = "UP";
		if(down) state = "DOWN";
		if(left) state = "LEFT";
		if(right) state = "RIGHT";
		if(attack) state = "ATTACK_SWORD_START";
		if(!(up || down || left || right || attack)) state = "IDLE";
	}

	public void keyPressed(int key)
	{
		if(key == KeyEvent.VK_D)
		{
			right = true;
		}
		if(key == KeyEvent.VK_A)
		{
			left = true;
		}
		if(key == KeyEvent.VK_W)
		{
			up = true;
		}
		if(key == KeyEvent.VK_S)
		{
			down = true;
		}
		if(key == KeyEvent.VK_SPACE)
		{
			attack = true;
		}
	}

	public void keyReleased(int key)
	{
		if(key == KeyEvent.VK_D)
		{
			right = false;
		}
		if(key == KeyEvent.VK_A)
		{
			left = false;
		}
		if(key == KeyEvent.VK_W)
		{
			up = false;
		}
		if(key == KeyEvent.VK_S)
		{
			down = false;
		}
		if(key == KeyEvent.VK_SPACE)
		{
			attack = false;
		}
	}
}
