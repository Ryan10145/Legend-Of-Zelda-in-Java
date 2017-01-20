package components.entity;

import components.Animation;
import components.OverWorld;
import components.entity.enemies.Enemy;
import components.entity.enemies.Octorok;
import components.map.MapItem;
import components.map.WarpTile;
import components.map.collectibles.Collectible;
import components.map.collectibles.Heart;
import components.map.collectibles.HeartContainer;
import components.weapons.Sword;
import utility.Images;
import utility.MathHelper;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Link extends Entity
{
	private int transitionAmountX;
	private int transitionAmountY;

	private int transitionVelX;
	private int transitionVelY;

	private boolean inputLeft;
	private boolean inputUp;
	private boolean inputRight;
	private boolean inputDown;
	private boolean inputAttack;

	private Sword sword;
	private int swordTimer;

	private final Animation walkUp;
	private final Animation walkRight;
	private final Animation walkDown;
	private final Animation walkLeft;

	private final BufferedImage[] swordAttack;

	private int healthContainers;
	private final int maxHealthContainers;

	public Link(OverWorld overWorld)
	{
		this.overWorld = overWorld;

		x = 160 + overWorld.getCameraX();
		y = 96 + overWorld.getCameraY();

		drawX = (int) Math.round(x);
		drawY = (int) Math.round(y);

		moveSpeed = 1.5;

		width = 16;
		height = 16;

		swordTimer = 0;

		walkUp = new Animation(5, true, Images.Link.LINK_UP, Images.Link.LINK_UP_2);
		walkRight = new Animation(5, true, Images.Link.LINK_RIGHT, Images.Link.LINK_RIGHT_2);
		walkDown = new Animation(5, true, Images.Link.LINK_DOWN, Images.Link.LINK_DOWN_2);
		walkLeft = new Animation(5, true, Images.Link.LINK_LEFT, Images.Link.LINK_LEFT_2);

		swordAttack = new BufferedImage[] {Images.Link.LINK_ATTACK_SWORD_UP, Images.Link.LINK_ATTACK_SWORD_RIGHT,
				                           Images.Link.LINK_ATTACK_SWORD_DOWN, Images.Link.LINK_ATTACK_SWORD_LEFT};

		state = "IDLE";

		direction = Direction.UP;

		health = 24;
		healthContainers = 3;
		maxHealthContainers = 16;
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
			velX = alignToGrid(x, 8);
			velY = -moveSpeed;
			direction = Direction.UP;

			walkUp.update();

			checkFreeMovement();
			break;
		case "RIGHT":
			velX = moveSpeed;
			velY = alignToGrid(y, 8);
			direction = Direction.RIGHT;

			walkRight.update();

			checkFreeMovement();
			break;
		case "DOWN":
			velX = alignToGrid(x, 8);
			velY = moveSpeed;
			direction = Direction.DOWN;

			walkDown.update();

			checkFreeMovement();
			break;
		case "LEFT":
			velX = -moveSpeed;
			velY = alignToGrid(y, 8);
			direction = Direction.LEFT;

			walkLeft.update();

			checkFreeMovement();
			break;
		case "ATTACK_SWORD_START":
			velX = 0;
			velY = 0;

			swordTimer = 16;

			state = "ATTACK_SWORD";
			break;
		case "ATTACK_SWORD":
			if(swordTimer == 9)
			{
				int[] drawingCoordinates = MathHelper.getSwordOffset((int) Math.round(x),
						(int) Math.round(y), 12, direction);
				sword = new Sword(drawingCoordinates[0], drawingCoordinates[1], direction, overWorld);
			}
			else if(swordTimer <= 2)
			{
				sword.retract();
			}

			if(swordTimer > 0) swordTimer--;
			else
			{
				state = "IDLE";
				sword = null;
			}
			break;
		case "TRANSITION":
			velX = 0;
			velY = 0;

			x += transitionVelX;
			y += transitionVelY;

			transitionAmountX += transitionVelX;
			transitionAmountY += transitionVelY;

			if(Math.abs(transitionAmountX) == 20) transitionVelX = 0;
			if(Math.abs(transitionAmountY) == 20) transitionVelY = 0;

			if(transitionVelX == 0 && transitionVelY == 0)
			{
				state = "IDLE";
				transitionAmountX = 0;
				transitionAmountY = 0;
			}
			break;
		default:
			System.out.println(state);
			break;
		}

		if(invincibilityFrames > 0) invincibilityFrames--;

		if(sword != null) sword.update();

		handleTileCollisions();
		if(invincibilityFrames == 0) handleEnemyCollisions();
		handleMapItemCollisions();
	}

	public void draw(Graphics2D g2d)
	{
		if(!(invincibilityFrames > 0 && invincibilityFrames % 3 == 0))
		{
			drawX = (int) Math.round(x) - overWorld.getCameraX();
			drawY = (int) Math.round(y) - overWorld.getCameraY();


			if(sword != null) sword.draw(g2d);

			switch(state)
			{
			case "IDLE":
				switch(direction)
				{
				case UP:
					walkUp.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case RIGHT:
					walkRight.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case DOWN:
					walkDown.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case LEFT:
					walkLeft.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				default:
					break;
				}
				break;
			case "UP":
				walkUp.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
				break;
			case "DOWN":
				walkDown.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
				break;
			case "RIGHT":
				walkRight.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
				break;
			case "LEFT":
				walkLeft.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
				break;
			case "ATTACK_SWORD_START":
				g2d.drawImage(swordAttack[direction.getInteger()], drawX - width / 2, drawY - height / 2, width, height, null);
				break;
			case "ATTACK_SWORD":
				g2d.drawImage(swordAttack[direction.getInteger()], drawX - width / 2, drawY - height / 2, width, height, null);
				break;
			case "TRANSITION":
				switch(direction)
				{
				case UP:
					walkUp.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case RIGHT:
					walkRight.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case DOWN:
					walkDown.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				case LEFT:
					walkLeft.draw(g2d, drawX - width / 2, drawY - height / 2, width, height);
					break;
				default:
					break;
				}
				break;
			default:
				g2d.setColor(Color.RED);
				g2d.drawRect(drawX - width / 2, drawY - height / 2, width, height);
				break;
			}
		}

//		drawDebug(g2d);
	}

	//Check for movement that you have when you can do anything, i.e. IDLE, or LEFT/RIGHT/UP/DOWN
	private void checkFreeMovement()
	{
		if(inputUp) state = "UP";
		if(inputDown) state = "DOWN";
		if(inputLeft) state = "LEFT";
		if(inputRight) state = "RIGHT";
		if(inputAttack) state = "ATTACK_SWORD_START";
		if(!(inputUp || inputDown || inputLeft || inputRight || inputAttack)) state = "IDLE";

		if(transitionVelX != 0 || transitionVelY != 0) 	state = "TRANSITION";
	}

	private void handleEnemyCollisions()
	{
		ArrayList<Enemy> enemies = overWorld.getEnemies();
		for(Enemy enemy : enemies)
		{
			if(checkCollisionWith(enemy))
			{
				health -= enemy.getDamage();
				invincibilityFrames = 30;
			}

			if(enemy instanceof Octorok)
			{
				Octorok octorok = (Octorok) enemy;

				if(octorok.getPelletCollisionBox() != null)
				{
					if(checkCollisionWith(octorok.getPelletCollisionBox()))
					{
						health -= octorok.getPelletDamage();
						invincibilityFrames = 30;
						octorok.removePellet();
					}
				}
			}
		}
	}

	private void handleMapItemCollisions()
	{
		WarpTile collidedWarpTile;
		char id;

		ArrayList<MapItem> mapItems = overWorld.getMapItems();
		Iterator iterator = mapItems.iterator();
		while(iterator.hasNext())
		{
			MapItem mapItem = (MapItem) iterator.next();
			if(mapItem instanceof Collectible)
			{
				Collectible collectible = (Collectible) mapItem;
				if(checkCollisionWith(collectible.getRectangle()))
				{
					if(collectible instanceof Heart)
					{
						Heart heart = (Heart) collectible;

						if(health != healthContainers * 8)
						{
							if(health + heart.getRestoreValue() >= healthContainers * 8)
							{
								health = healthContainers * 8;
								iterator.remove();
							}
							else
							{
								health += heart.getRestoreValue();
								iterator.remove();
							}
						}
					}
					else if(collectible instanceof HeartContainer)
					{
						if(healthContainers + 1 < maxHealthContainers)
						{
							healthContainers++;
							health = healthContainers * 8;
							iterator.remove();
						}
					}
				}
			}
			else if(mapItem instanceof WarpTile)
			{
				if(checkCollisionWith(mapItem.getRectangle()))
				{
					collidedWarpTile = (WarpTile) mapItem;
					id = collidedWarpTile.getId();
					teleportToWarpTile(collidedWarpTile, id);
					break;
				}
			}
		}
	}

	private void teleportToWarpTile(WarpTile destination, char id)
	{
		for(MapItem mapItem : overWorld.getMapItems())
		{
			if(mapItem instanceof WarpTile)
			{
				WarpTile warpTile = (WarpTile) mapItem;

				int overWorldWidth = overWorld.getWidthOfTile();
				int overWorldHeight = overWorld.getHeightOfTile();

				if(warpTile.getId() == Character.toUpperCase(id) && !warpTile.equals(destination) &&
						warpTile.getColumn(overWorldWidth) != destination.getColumn(overWorldWidth) &&
						warpTile.getRow(overWorldHeight) != destination.getRow(overWorldHeight))
				{
					x = warpTile.getX() + overWorld.getWidthOfTile() / 2;
					y = warpTile.getY() + overWorld.getHeightOfTile() / 2;

					switch(direction)
					{
					case RIGHT:
						x += warpTile.getWidth();
						break;
					case UP:
						y -= warpTile.getHeight();
						break;
					case LEFT:
						x -= warpTile.getWidth();
						break;
					case DOWN:
						y += warpTile.getHeight();
						break;
					}

					overWorld.setCameraX((int) Math.round(x) / overWorld.getMapWidth() * overWorld.getMapWidth());
					overWorld.setCameraY((int) Math.round(y) / overWorld.getMapHeight() * overWorld.getMapHeight());
				}
			}
		}
	}

	public void setKeyVariable(int key, boolean bool)
	{
		if(key == KeyEvent.VK_D) inputRight = bool;
		if(key == KeyEvent.VK_A) inputLeft = bool;
		if(key == KeyEvent.VK_W) inputUp = bool;
		if(key == KeyEvent.VK_S) inputDown = bool;
		if(key == KeyEvent.VK_SPACE) inputAttack = bool;
	}

	public Sword getSword()
	{
		return sword;
	}

	public int getHealthContainers()
	{
		return healthContainers;
	}
	public void setTransitionVector(int transitionVelX, int transitionVelY)
	{
		this.transitionVelX = transitionVelX;
		this.transitionVelY = transitionVelY;
	}
}