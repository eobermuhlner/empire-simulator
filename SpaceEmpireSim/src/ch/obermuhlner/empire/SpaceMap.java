package ch.obermuhlner.empire;

public class SpaceMap {
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;

	public final MapCell[] cells;
	
	public SpaceMap(int sizeX, int sizeY, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		cells = new MapCell[sizeX * sizeY * sizeZ];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = new MapCell();
		}
	}

	public MapCell get(Coord coord) {
		return get(coord.x, coord.y, coord.z);
	}
	
	public MapCell get(int x, int y, int z) {
		return cells[x + y * sizeX + z * (sizeX*sizeY)];
	}
	
	public MapCell[] getAll() {
		return cells;
	}
	
	
}
