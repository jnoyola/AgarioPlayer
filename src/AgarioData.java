import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;


public class AgarioData {

	private User[] leaderboard = new User[10];
	private HashMap<Integer, Cell> cells = new HashMap<Integer, Cell>();
	
	public class User {
		public int id;
		public String name;
		public User(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	public class Cell {
		public int id;
		public int x;
		public int y;
		public short size;
		public Color color;
		public String name;
		public Cell(int id, int x, int y, short size, Color color, String name) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.size = size;
			this.color = color;
			this.name = name;
		}
	}
	
	public void setLeader(int index, int id, String name) {
		leaderboard[index] = new User(id, name);
	}
	
	public void printLeaderboard() {
		for (User leader : leaderboard) {
			if (leader == null) continue;
			if (leader.name.isEmpty())
				System.out.println("An unnamed cell");
			else
				System.out.println(leader.name);
		}
		System.out.println("");
		System.out.println("");
	}
	
	public Cell getCell(int id) {
		return cells.get(id);
	}
	
	public Collection<Cell> getCells() {
		return cells.values();
	}
	
	public void clearCells() {
		cells.clear();
	}
	
	public Cell addCell(int id, int x, int y, short size, Color color, String name) {
		Cell cell = new Cell(id, x, y, size, color, name);
		cells.put(id, cell);
		return cell;
	}
}
