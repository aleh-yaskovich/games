package by.aleh.yaskovich.entity;

public class Ship {
	
	private int size;
	private int[] coordinates;
	private int[] spaceAround;
	
	public Ship() {
		this.size = 0;
		this.coordinates = null;
		this.spaceAround = null;
	}
	
	public Ship(int size, int[] coordinates, int[] spaceAround) {
		this.size = size;
		this.coordinates = coordinates;
		this.spaceAround = spaceAround;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(int[] coordinates) {
		this.coordinates = coordinates;
	}

	public int[] getSpaceAround() {
		return spaceAround;
	}

	public void setSpaceAround(int[] spaceAround) {
		this.spaceAround = spaceAround;
	}
	
}

