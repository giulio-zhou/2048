public class Tile {
	int value;
	int xPosition;
	int yPosition;
	boolean merged;

	public Tile(int value, int xPosition, int yPosition) {
		this.value = value;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.merged = false;
	}

	public Tile(int value, int xPosition, int yPosition, boolean merged) {
		this.value = value;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.merged = merged; 
	}

	public void setPosition(int new_x, int new_y) {
		this.xPosition = new_x;
		this.yPosition = new_y;
	}

	public int getX() {
		return xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public Tile combine(Tile other_tile) {
		return new Tile(this.value*2, other_tile.xPosition, other_tile.yPosition, true);
	}

	public int getValue() {
		return this.value;
	}

	public boolean hasMerged() {
		return this.merged;
	}
	
	public String toString() {
		return Integer.toString(value) + " " + Integer.toString(xPosition) + " " + Integer.toString(yPosition);
	}	
}
