package ch.obermuhlner.empire;

public class Coord {

	public final int x;
	public final int y;
	public final int z;
	
	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}
}
