
public class AgarioData {
	
	public class User {
		public int id;
		public String name;
		public User(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	public User[] leaderboard = new User[10];
	
	public void setLeader(int index, int id, String name) {
		leaderboard[index] = new User(id, name);
	}
	
	public void printLeaderboard() {
		for (User leader : leaderboard) {
			if (leader.name.isEmpty())
				System.out.println("An unnamed cell");
			else
				System.out.println(leader.name);
		}
		System.out.println("");
		System.out.println("");
	}
}
