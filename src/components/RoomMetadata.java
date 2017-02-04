package components;

import components.entity.enemies.Enemy;
import components.map.WarpTile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class RoomMetadata
{
	private int id;
	private OverWorld overWorld;

	private ArrayList<Enemy> enemies;
	private ArrayList<WarpTile> warpTiles;

	private String roomType;
	private String music;

	RoomMetadata(int id, OverWorld overWorld)
	{
		this.id = id;
		this.overWorld = overWorld;

		loadMetadata();
	}

	private void loadMetadata()
	{
		Document metaData = overWorld.getMetadataDocument();

		Element thisRoom = null;

		NodeList rooms = metaData.getElementsByTagName("ROOM");
		for(int roomIndex = 0; roomIndex < rooms.getLength(); roomIndex++)
		{
			Element room = (Element) rooms.item(roomIndex);
			if(room.getAttribute("id").equals(id / 10 + "-" + id % 10))
			{
				thisRoom = room;
				break;
			}
		}

		if(thisRoom != null)
		{
			roomType = thisRoom.getElementsByTagName("ROOM-TYPE").item(0).getTextContent();
			music = thisRoom.getElementsByTagName("MUSIC").item(0).getTextContent();

			enemies = new ArrayList<>();
			warpTiles = new ArrayList<>();

			Element enemiesElement = (Element) thisRoom.getElementsByTagName("ENEMIES").item(0);
			NodeList enemiesList = enemiesElement.getElementsByTagName("ENEMY");
			for(int enemyIndex = 0; enemyIndex < enemiesList.getLength(); enemyIndex++)
			{
				Element enemy = (Element) enemiesList.item(enemyIndex);
				String type = enemy.getElementsByTagName("TYPE").item(0).getTextContent();
				int col = Integer.parseInt(enemy.getElementsByTagName("COL").item(0).getTextContent());
				int row = Integer.parseInt(enemy.getElementsByTagName("ROW").item(0).getTextContent());
				System.out.println(col);
				enemies.add(overWorld.getMapFactory().buildEnemy(type, col, row));
			}
		}
	}

	public ArrayList<Enemy> getEnemies()
	{
		return enemies;
	}

	public ArrayList<WarpTile> getWarpTiles()
	{
		return warpTiles;
	}

	public String getRoomType()
	{
		return roomType;
	}

	public String getMusic()
	{
		return music;
	}
}