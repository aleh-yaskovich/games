package by.aleh.yaskovich.service;

import java.awt.Color;
import java.util.*;
import javax.swing.*;
import by.aleh.yaskovich.entity.Ship;

public class Service {
	
	// Переменные для логики бота
	private static List<Integer> isHit = new ArrayList<Integer>(); // Если бот попал
	private static List<Integer> tmpHits = new ArrayList<Integer>(); // Если бот попал два раза
	private static boolean[] shots = new boolean[100]; // Массив отстрелляных координат
	private static int[] shipsSizes = {4,3,3,2,2,2,1,1,1,1};
	private static SortedSet<Integer> humanShipCoordinates = new TreeSet(); // Координаты будущего корабля
	private static List<Integer> aroundCoordHuman = new ArrayList<Integer>(); // Клетки, на которые можно нажимать
	private static int[][] shipMapHuman = new int[10][10];
	
	// Геттеры и сеттеры
	public static List<Integer> getIsHit() { return isHit; }
	public static void setIsHit(List<Integer> isHit) { Service.isHit = isHit; }
	public static List<Integer> getTmpHits() { return tmpHits; }
	public static void setTmpHits(List<Integer> tmpHits) { Service.tmpHits = tmpHits; }
	public static boolean[] getShots() { return shots; }
	public static void setShots(boolean[] shots) { Service.shots = shots; }
	public static SortedSet<Integer> getHumanShipCoordinates() { return humanShipCoordinates; }
	public static void setHumanShipCoordinates(SortedSet<Integer> humanShipCoordinates) { Service.humanShipCoordinates = humanShipCoordinates; }
	public static List<Integer> getAroundCoordHuman() { return aroundCoordHuman; }
	public static void setAroundCoordHuman(List<Integer> aroundCoordHuman) { Service.aroundCoordHuman = aroundCoordHuman; }
	public static int[][] getShipMapHuman() { return shipMapHuman; }
	public static void setShipMapHuman(int[][] shipMapHuman) { Service.shipMapHuman = shipMapHuman; }
	///////////////////////////////////////////////////
	
	// Расставляем корабли самостоятельно
	public static void setShips(int shot, JButton[] buttons, List<Ship> humanShipsList) {
		shipMapHuman[shot/10][shot%10] = 1;
		humanShipCoordinates.add(shot);
		addAroundCoordHuman(shot);
		for(JButton button : buttons) {
			button.setBackground(Color.WHITE);
			button.setEnabled(false);
		}
		for(Ship ship : humanShipsList) {
			for(int coordinate : ship.getCoordinates()) { buttons[coordinate].setBackground(Color.GREEN); }
			for(int space : ship.getSpaceAround()) { buttons[space].setBackground(new Color(225, 225, 225)); }
		}
		for(int coordinate : humanShipCoordinates) { buttons[coordinate].setBackground(Color.BLUE); }
		for(int around : aroundCoordHuman) {
			buttons[around].setBackground(Color.LIGHT_GRAY);
			buttons[around].setEnabled(true);
		}
		aroundCoordHuman.clear();
		if(humanShipCoordinates.size() == shipsSizes[humanShipsList.size()]) {
			int[] coordinates = new int[humanShipCoordinates.size()];
			Iterator iterator = humanShipCoordinates.iterator();
			int count = 0;
			while(iterator.hasNext()) {
				coordinates[count] = (int)iterator.next();
				count++;
			}
			int[] spaceAround = createSpaceAround();
			humanShipsList.add(new Ship(shipsSizes[humanShipsList.size()], coordinates, spaceAround));			
			humanShipCoordinates.clear();
			for(JButton button : buttons) {
				button.setBackground(Color.WHITE);
				button.setEnabled(true);
			}
			for(Ship ship : humanShipsList) {
				for(int coordinate : ship.getCoordinates()) {
					buttons[coordinate].setBackground(Color.GREEN);
					buttons[coordinate].setEnabled(false);
				}
				for(int space : ship.getSpaceAround()) {
					buttons[space].setBackground(new Color(225, 225, 225));
					buttons[space].setEnabled(false);
				}
			}
		}
	}
	
	// Определяем клетки, для дальнейшего размещения корабля
	private static void addAroundCoordHuman(int shot) {
		if(humanShipCoordinates.size() > 1) {
			int first = humanShipCoordinates.first();
			int last = humanShipCoordinates.last();
			if((last - first) >=1 && (last - first) < 5) {
				if(first-1 >= 0 && (first/10 == (first-1)/10)) {if(shipMapHuman[(first-1)/10][(first-1)%10] == 0) aroundCoordHuman.add(first-1);}
				if(last+1 < 100 && (last/10 == (last+1)/10)) {if(shipMapHuman[(last+1)/10][(last+1)%10] == 0) aroundCoordHuman.add(last+1);}
			} else {
				if(first-10 >= 0 && Math.abs((first-10)/10 - first/10) == 1) {if(shipMapHuman[(first-10)/10][(first-10)%10] == 0) aroundCoordHuman.add(first-10);}
				if(last+10 < 100 && Math.abs((last+10)/10 - last/10) == 1) {if(shipMapHuman[(last+10)/10][(last+10)%10] == 0) aroundCoordHuman.add(last+10);}
			}
			
		} else {
			int up = shot - 10;
			int right = shot + 1;
			int down = shot + 10;
			int left = shot - 1;
			
			if(up >= 0 && (shot/10 - up/10) == 1) { if(shipMapHuman[up/10][up%10] == 0) aroundCoordHuman.add(up); }
			if(right < 100 && (shot/10 == right/10)) { if(shipMapHuman[right/10][right%10] == 0) aroundCoordHuman.add(right); }
			if(down < 100 && (shot/10 - down/10) == -1) { if(shipMapHuman[down/10][down%10] == 0) aroundCoordHuman.add(down); }
			if(left >= 0 && (shot/10 == left/10)) { if(shipMapHuman[left/10][left%10] == 0) aroundCoordHuman.add(left); }
		}
	}
	
	// Определяем пространство вокруг корабля
	private static int[] createSpaceAround() {
		
		SortedSet<Integer> spaceAroundSet = new TreeSet();
		Iterator iterator = humanShipCoordinates.iterator();
		while(iterator.hasNext()) {
			int coordinate = (int)iterator.next();
			if(coordinate-11 >= 0 && coordinate%10 != 0) {
				if(shipMapHuman[(coordinate-11)/10][(coordinate-11)%10] != 1) {
					shipMapHuman[(coordinate-11)/10][(coordinate-11)%10] = 2;
					spaceAroundSet.add(coordinate-11);
				}
			}
			if(coordinate-10 >= 0) {
				if(shipMapHuman[(coordinate-10)/10][(coordinate-10)%10] != 1) {
					shipMapHuman[(coordinate-10)/10][(coordinate-10)%10] = 2;
					spaceAroundSet.add(coordinate-10);
				}
			}
			if(coordinate-9 >= 0 && coordinate%10 != 9) {
				if(shipMapHuman[(coordinate-9)/10][(coordinate-9)%10] != 1) {
					shipMapHuman[(coordinate-9)/10][(coordinate-9)%10] = 2;
					spaceAroundSet.add(coordinate-9);
				}
			}
			if(coordinate+9 < 100 && coordinate%10 != 0) {
				if(shipMapHuman[(coordinate+9)/10][(coordinate+9)%10] != 1) {
					shipMapHuman[(coordinate+9)/10][(coordinate+9)%10] = 2;
					spaceAroundSet.add(coordinate+9);
				}
			}
			if(coordinate+10 < 100) {
				if(shipMapHuman[(coordinate+10)/10][(coordinate+10)%10] != 1) {
					shipMapHuman[(coordinate+10)/10][(coordinate+10)%10] = 2;
					spaceAroundSet.add(coordinate+10);
				}
			}
			if(coordinate+11 < 100 && coordinate%10 != 9) {
				if(shipMapHuman[(coordinate+11)/10][(coordinate+11)%10] != 1) {
					shipMapHuman[(coordinate+11)/10][(coordinate+11)%10] = 2;
					spaceAroundSet.add(coordinate+11);
				}
			}
			if(coordinate+1 < 100 && coordinate%10 != 9) {
				if(shipMapHuman[(coordinate+1)/10][(coordinate+1)%10] != 1) {
					shipMapHuman[(coordinate+1)/10][(coordinate+1)%10] = 2;
					spaceAroundSet.add(coordinate+1);
				}
			}
			if(coordinate-1 >= 0 && coordinate%10 != 0) {
				if(shipMapHuman[(coordinate-1)/10][(coordinate-1)%10] != 1) {
					shipMapHuman[(coordinate-1)/10][(coordinate-1)%10] = 2;
					spaceAroundSet.add(coordinate-1);
				}
			}
		}
		iterator = spaceAroundSet.iterator();
		int[] spaceAround = new int[spaceAroundSet.size()];
		int count = 0;
		while(iterator.hasNext()) {
			spaceAround[count] = (int)iterator.next();
			count++;
		}
		return spaceAround;
	}
	
	
	// Проверяем, попал или нет
	public static boolean check(List<Ship> ships, int shot) {
		boolean res = false;
		for(Ship ship : ships) {
			for(int coordinate : ship.getCoordinates()) {
				if(coordinate == shot) {
					int shipSize = ship.getSize();
					ship.setSize(--shipSize);
					res = true;
				}
			}
		}
		return res;
	}
	
	// Проверяем, потопил или нет
	public static boolean kill(List<Ship> ships) {
		boolean res = false;
		for(Ship ship : ships) { if(ship.getSize() == 0) res = true; }
		return res;
	}
	
	// Отмечаем пространство вокруг корабля
	public static void updateSpaceAround(int shot, List<Ship> ships, JButton[] buttons) {
		Ship ship = null;
		for(Ship s : ships) { if(s.getSize() == 0) ship = s; }
		for(int space : ship.getSpaceAround()) {
			buttons[space].setBackground(new Color(225, 225, 225));
			buttons[space].setEnabled(false);
		}
		ships.remove(ship);
	}
	
	// Отмечаем клетки вокруг потопленного корабля как использованные (для бота)
	private static void updateShots(int shot, List<Ship> ships) {
		Ship ship = null;
		for(Ship s : ships) { if(s.getSize() == 0) ship = s; }
		for(int space : ship.getSpaceAround()) { shots[space] = true; }
	}
	
	// Блокируем все кнопки на большой панели
	public static void blockBigPanel(JButton[] botJButtonsArray) {
		for(JButton button : botJButtonsArray) button.setEnabled(false);
	}
	
	// Логика бота
	public static void botLogic(List<Ship> humanShipsList, JButton[] humanJButtonsArray, JButton[] botJButtonsArray, JLabel myShipsLabel, JLabel statusLabel) {
		boolean res = true;
		while(res) {
			// Создаем координату выстрела
			int shot;
			if(isHit.isEmpty()) { shot = (int)(Math.random()*100); }
			else {
				shot = isHit.get(0); 
				isHit.remove(0);
			}
			
			if(!shots[shot]) {
				shots[shot] = true;
				if(check(humanShipsList, shot)) {
					humanJButtonsArray[shot].setBackground(Color.RED);
					// Проверяем, потопил или нет
					if(kill(humanShipsList)) {
						isHit.clear();
						tmpHits.clear();
						updateShots(shot, humanShipsList);
						updateSpaceAround(shot, humanShipsList, humanJButtonsArray);
						myShipsLabel.setText("Ваши корабли: "+humanShipsList.size());
						// Если бот потопил все мои корабли
						if(humanShipsList.isEmpty()) {
							blockBigPanel(botJButtonsArray);
							statusLabel.setText("Состояние: Бот победил!");
							res = false;
						}
					} else { // Если попал, но не убил
						if(isHit.isEmpty()) { addIsHit(shot); }
						tmpHits.add(shot);
						if(tmpHits.size() == 2) {
							updateIsHit();
							tmpHits.clear();
						}
					}
				} else {
					humanJButtonsArray[shot].setBackground(new Color(225, 225, 225));
					res = false;
				}
			}
		}
	}
	
	// Если попал, но не убил, заполняем список (для бота)
	private static void addIsHit(int shot) {
		int up = shot - 10;
		int right = shot + 1;
		int down = shot + 10;
		int left = shot - 1;
		
		if(up >= 0 && (shot/10 - up/10) == 1) {
			if(!shots[up]) isHit.add(up);
		}
		if(right < 100 && (shot/10 == right/10)) {
			if(!shots[right]) isHit.add(right);
		}
		if(down < 100 && (shot/10 - down/10) == -1) {
			if(!shots[down]) isHit.add(down);
		}
		if(left >= 0 && (shot/10 == left/10)) {
			if(!shots[left]) isHit.add(left);
		}
	}
	
	// Меняем isHit, если два попадания
	private static void updateIsHit() {
		isHit.clear();
		int shot1 = tmpHits.get(0); // допустим shot1 = 48
		int shot2 = tmpHits.get(1); // допустим shot2 = 49
		
		if(shot1-shot2 == -1) {
			if(shot1-1 >= 0 && (shot1-1)/10 == shot1/10) { if(!shots[shot1-1]) isHit.add(shot1-1); }
			if(shot2+1 < 100 && (shot2+1)/10 == shot2/10) { if(!shots[shot2+1]) isHit.add(shot2+1); }
			if(shot1-2 >= 0 && (shot1-2)/10 == shot1/10) { if(!shots[shot1-2]) isHit.add(shot1-2); }
			if(shot2+2 < 100 && (shot2+2)/10 == shot2/10) { if(!shots[shot2+2]) isHit.add(shot2+2); }
		}
		
		if(shot1-shot2 == 1) {
			if(shot2-1 >= 0 && (shot2-1)/10 == shot2/10) { if(!shots[shot2-1]) isHit.add(shot2-1); }
			if(shot2-2 >= 0 && (shot2-2)/10 == shot2/10) { if(!shots[shot2-2]) isHit.add(shot2-2); }			
		}
		
		if(shot1-shot2 == -10) {
			if(shot2+10 < 100) { if(!shots[shot2+10]) isHit.add(shot2+10); }
			if(shot2+20 < 100) { if(!shots[shot2+20]) isHit.add(shot2+20); }	
		}
		
		if(shot1-shot2 == 10) {
			if(shot1+10 < 100) { if(!shots[shot1+10]) isHit.add(shot1+10); }
			if(shot2-10 >= 0) { if(!shots[shot2-10]) isHit.add(shot2-10); }
			if(shot1+20 < 100) { if(!shots[shot1+20]) isHit.add(shot1+20); }
			if(shot2-20 >= 0) { if(!shots[shot2-20]) isHit.add(shot2-20); }
		}
	}	
	
}
