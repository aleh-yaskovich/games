package by.aleh.yaskovich.entity;

import java.util.*;

public class Ships {
	
	private int[] shipSizes;
	private int[][] shipsMap; // временный массив
	private List<Ship> ships;
	
	public Ships() {
		this.shipSizes = new int[]{4,3,3,2,2,2,1,1,1,1};
		this.shipsMap = new int[10][10];
		this.ships = createShips();
	}
	
	public Ships(List<Ship> ships) {
		this.shipSizes = new int[]{4,3,3,2,2,2,1,1,1,1};
		this.shipsMap = new int[10][10];
		this.ships = ships;
	}

	public List<Ship> getShips() {
		return ships;
	}

	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}

	// метод для создания списка кораблей List<Ship> ships
	private List<Ship> createShips() {
		List<Ship> list = new ArrayList<Ship>();
		for(int i = 0; i < shipSizes.length; i++) {
			int shipSize = shipSizes[i];
			// создаем массив координат корабля
			int[] coordinates = createCoordinates(shipsMap, shipSize);
			// создаем массив координат вокруг корабля
			int[] spaceAround = createSpaceAround(shipsMap, coordinates);
			// создаем новый объект Ship и добавляем его в список
			list.add(new Ship(shipSize, coordinates, spaceAround));
		}
		return list;
	}
	
	// создаем массив координат корабля
	private int[] createCoordinates(int[][] shipsMap, int shipSize) {
		int[] coordinates = new int[shipSize];
		boolean res = false;
		while(!res) {
			// определяем ориентацию: 0 - горизонтальная, 1 - вертикальная
			int orientation = (int)Math.round(Math.random());
			// Создаем координаты начальной точки корабля
			int rand = (int)(Math.random()*100);
			// Создаем временную переменную для проверки и массив с координатами
			boolean check = true;
			int[] tmpCoord = new int[shipSize];
			// Пробуем создать корабль
			for(int k = 0; k < shipSize; k++) {
				if (shipsMap[rand/10][rand%10] == 0) {
					tmpCoord[k] = rand;
					if(orientation == 0 && (rand+1) < 100 && rand/10 == (rand+1)/10) { rand++; }
					else if (orientation == 1 && (rand+10) < 100) { rand = rand+10; }
					else {
						check = false;
						break;
					}
				} else {
					check = false;
					break;
				}
			}		
				// Проверяем результат цикла for
				if(check) {
					for(int j = 0; j < tmpCoord.length; j++) {
						coordinates[j] = tmpCoord[j];
						shipsMap[tmpCoord[j]/10][tmpCoord[j]%10] = 1;
					}
					res = true;
				}
		}
		return coordinates;
	}
	
	// создаем массив координат вокруг корабля
	private int[] createSpaceAround(int[][] shipsMap, int[] coordinates) {
		SortedSet<Integer> spaceAroundSet = new TreeSet<Integer>();
		for(int i = 0; i < coordinates.length; i++) {
			int coordinate = coordinates[i];
			if(coordinate-11 >= 0 && coordinate%10 != 0) {
				if(shipsMap[(coordinate-11)/10][(coordinate-11)%10] != 1) {
					shipsMap[(coordinate-11)/10][(coordinate-11)%10] = 2;
					spaceAroundSet.add(coordinate-11);
				}
			}
			if(coordinate-10 >= 0) {
				if(shipsMap[(coordinate-10)/10][(coordinate-10)%10] != 1) {
					shipsMap[(coordinate-10)/10][(coordinate-10)%10] = 2;
					spaceAroundSet.add(coordinate-10);
				}
			}
			if(coordinate-9 >= 0 && coordinate%10 != 9) {
				if(shipsMap[(coordinate-9)/10][(coordinate-9)%10] != 1) {
					shipsMap[(coordinate-9)/10][(coordinate-9)%10] = 2;
					spaceAroundSet.add(coordinate-9);
				}
			}
			if(coordinate+9 < 100 && coordinate%10 != 0) {
				if(shipsMap[(coordinate+9)/10][(coordinate+9)%10] != 1) {
					shipsMap[(coordinate+9)/10][(coordinate+9)%10] = 2;
					spaceAroundSet.add(coordinate+9);
				}
			}
			if(coordinate+10 < 100) {
				if(shipsMap[(coordinate+10)/10][(coordinate+10)%10] != 1) {
					shipsMap[(coordinate+10)/10][(coordinate+10)%10] = 2;
					spaceAroundSet.add(coordinate+10);
				}
			}
			if(coordinate+11 < 100 && coordinate%10 != 9) {
				if(shipsMap[(coordinate+11)/10][(coordinate+11)%10] != 1) {
					shipsMap[(coordinate+11)/10][(coordinate+11)%10] = 2;
					spaceAroundSet.add(coordinate+11);
				}
			}
			if(coordinate+1 < 100 && coordinate%10 != 9) {
				if(shipsMap[(coordinate+1)/10][(coordinate+1)%10] != 1) {
					shipsMap[(coordinate+1)/10][(coordinate+1)%10] = 2;
					spaceAroundSet.add(coordinate+1);
				}
			}
			if(coordinate-1 >= 0 && coordinate%10 != 0) {
				if(shipsMap[(coordinate-1)/10][(coordinate-1)%10] != 1) {
					shipsMap[(coordinate-1)/10][(coordinate-1)%10] = 2;
					spaceAroundSet.add(coordinate-1);
				}
			}
		}
		int[] spaceAround = new int[spaceAroundSet.size()];
		Iterator<Integer> iterator = spaceAroundSet.iterator();
		int count = 0;
		while(iterator.hasNext()) {
			spaceAround[count] = (int)iterator.next();
			count++;
		}
		return spaceAround;
	}
	
}

