package by.aleh.yaskovich.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

import by.aleh.yaskovich.entity.Ship;
import by.aleh.yaskovich.entity.Ships;
import by.aleh.yaskovich.service.Service;

public class Gui {
	
	private List<Ship> humanShipsList;
	private JButton[] humanJButtonsArray;
	private JButton[] tmpHumanJButtonsArray;
	
	private Ships botShips;
	private List<Ship> botShipsList;
	private JButton[] botJButtonsArray;
	
	private JFrame frame;
	private JLabel myShipsLabel;
	private JLabel botShipsLabel;
	private JLabel statusLabel;
	private JPanel myShipsPanel;
	private JPanel botShipsPanel;	
	private JPanel buttonsPanel;
	private JButton buttonNew;
	private JButton buttonSetShips;
	
	public Gui() {
		this.frame = new JFrame("Морской бой");
		this.buttonNew = new JButton("Начать заново");
		this.buttonSetShips = new JButton("Расставить самому");
		
		this.humanShipsList = new Ships().getShips();
		this.humanJButtonsArray = createJButtonArray(false);
		this.tmpHumanJButtonsArray = createJButtonArray(true);
		
		this.botShips = new Ships();
		this.botShipsList = botShips.getShips();
		this.botJButtonsArray = createJButtonArray(true);
	}

	class MyButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			String shotStr = ((Component)source).getName();
			int shot = Integer.parseInt(shotStr);
			statusLabel.setText("Состояние: Игра началась!");
			buttonSetShips.setEnabled(false);
			
			if(Service.check(botShipsList, shot)) {
				botJButtonsArray[shot].setBackground(Color.RED);
				botJButtonsArray[shot].setEnabled(false);
				if(Service.kill(botShipsList)) {
					Service.updateSpaceAround(shot, botShipsList, botJButtonsArray);
					botShipsLabel.setText("Корабли бота: "+botShipsList.size());
				}
				if(botShipsList.isEmpty()) {
					Service.blockBigPanel(botJButtonsArray);
					statusLabel.setText("Состояние: Вы победили!");
				}
			} else {
				botJButtonsArray[shot].setBackground(new Color(225, 225, 225));
				botJButtonsArray[shot].setEnabled(false);
				Service.botLogic(humanShipsList, humanJButtonsArray, botJButtonsArray, myShipsLabel, statusLabel);
			}
		}
	}
	
	class NewButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			humanShipsList = new Ships().getShips();
			humanJButtonsArray = createJButtonArray(false);
			
			botShips = new Ships();
			botShipsList = botShips.getShips();
			botJButtonsArray = createJButtonArray(true);
			restart();
			
			botShipsPanel.removeAll();
			stuffBotShipsPanel();
			
			myShipsPanel.removeAll();
			checkHumanShips();
			for(int i = 0; i < 100; i++) {
				humanJButtonsArray[i].setPreferredSize(new Dimension(20, 20));
				myShipsPanel.add(humanJButtonsArray[i]);
			}
			
			myShipsLabel.setText("Ваши корабли: "+humanShipsList.size());
			botShipsLabel.setText("Корабли бота: "+botShipsList.size());
			statusLabel.setText("Состояние: Новая игра");
			buttonSetShips.setEnabled(true);
			
			frame.pack();
		}
	}
	
	class SetShipsButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			humanShipsList.clear();
			tmpHumanJButtonsArray = createJButtonArray(true); 
			
			botShipsPanel.removeAll();
			String[] columns = {"А","Б","В","Г","Д","Е","Ж","З","И","К"};
			botShipsPanel.add(new JLabel(""));
			int count = 0;
			for(int i = 0; i < 120; i++) {
				if(i < 10) { botShipsPanel.add(new JLabel(columns[i], SwingConstants.CENTER)); }
				else if((i+1)%11 == 0) { botShipsPanel.add(new JLabel(((i+1)/11)+"", SwingConstants.CENTER)); }
				else {
					tmpHumanJButtonsArray[count].setPreferredSize(new Dimension(35, 35));
					tmpHumanJButtonsArray[count].addActionListener(new SetShipButtonListener());
					botShipsPanel.add(tmpHumanJButtonsArray[count]);
					count++;
				}
			}
			
			myShipsPanel.removeAll();
			checkHumanShips();
			for(int i = 0; i < 100; i++) {
				humanJButtonsArray[i].setPreferredSize(new Dimension(20, 20));
				myShipsPanel.add(humanJButtonsArray[i]);
			}
			
			myShipsLabel.setText("Ваши корабли: "+humanShipsList.size());
			botShipsLabel.setText("Корабли бота: "+botShipsList.size());
			statusLabel.setText("Состояние: Расставляем корабли");
			buttonSetShips.setEnabled(true);

			frame.pack();
		}
	}
	
	class SetShipButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			String shotStr = ((Component)source).getName();
			int shot = Integer.parseInt(shotStr);
			Service.setShips(shot, tmpHumanJButtonsArray, humanShipsList);
			myShipsLabel.setText("Ваши корабли: "+humanShipsList.size());
			
			if(humanShipsList.size() == 10) {
				humanJButtonsArray = createJButtonArray(false);
				
				botShips = new Ships();
				botShipsList = botShips.getShips();
				botJButtonsArray = createJButtonArray(true);
				restart();
				
				botShipsPanel.removeAll();
				stuffBotShipsPanel();
				
				myShipsPanel.removeAll();
				checkHumanShips();
				for(int i = 0; i < 100; i++) {
					humanJButtonsArray[i].setPreferredSize(new Dimension(20, 20));
					myShipsPanel.add(humanJButtonsArray[i]);
				}
				
				myShipsLabel.setText("Ваши корабли: "+humanShipsList.size());
				botShipsLabel.setText("Корабли бота: "+botShipsList.size());
				statusLabel.setText("Готово. Можно начинать!");
				buttonSetShips.setEnabled(true);
				
				frame.pack();
			}
		}
	}
	
	

	public void go() {
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
		JPanel bigPanel = new JPanel();
		botShipsPanel = new JPanel(new GridLayout(11,11));
		stuffBotShipsPanel();
		bigPanel.add(botShipsPanel);
		
		JPanel smallPanel = new JPanel();
		smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
		smallPanel.setBorder(BorderFactory.createEmptyBorder(0,15,0,20));
		myShipsPanel = new JPanel(new GridLayout(10,10));
		myShipsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,8,0));
		checkHumanShips();
		for(int i = 0; i < 100; i++) {
			humanJButtonsArray[i].setPreferredSize(new Dimension(20, 20));
			myShipsPanel.add(humanJButtonsArray[i]);
		}
		
		myShipsLabel = new JLabel("Ваши корабли: "+humanShipsList.size());
		botShipsLabel = new JLabel("Корабли бота: "+botShipsList.size());
		statusLabel = new JLabel("Состояние: Новая игра");
		JPanel labelPanel = new JPanel(new GridLayout(3,1));
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0,0,14,0));
		labelPanel.add(myShipsLabel);
		labelPanel.add(botShipsLabel);
		labelPanel.add(statusLabel);
		
		buttonSetShips.addActionListener(new SetShipsButtonListener());
		buttonNew.addActionListener(new NewButtonListener());
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(2,1));
		buttonsPanel.add(buttonNew);
		buttonsPanel.add(buttonSetShips);
		
		smallPanel.add(myShipsPanel);
		smallPanel.add(labelPanel);
		smallPanel.add(buttonsPanel);
		
		panel.add(bigPanel);
		panel.add(smallPanel);
		
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(20, 20);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	// Создаем массив кнопок
	private JButton[] createJButtonArray(boolean active) {
		JButton[] buttons = new JButton[100];
		for(int i = 0; i < 100; i++) {
			JButton button = new JButton();
			button.setName(i+"");
			button.setEnabled(active);
			button.setBackground(Color.WHITE);
			buttons[i] = button;
		}
		return buttons;
	}
	
	//
	private void stuffBotShipsPanel() {
		String[] columns = {"А","Б","В","Г","Д","Е","Ж","З","И","К"};
		botShipsPanel.add(new JLabel(""));
		int count = 0;
		for(int i = 0; i < 120; i++) {
			if(i < 10) { botShipsPanel.add(new JLabel(columns[i], SwingConstants.CENTER)); }
			else if((i+1)%11 == 0) { botShipsPanel.add(new JLabel(((i+1)/11)+"", SwingConstants.CENTER)); }
			else {
				botJButtonsArray[count].setPreferredSize(new Dimension(35, 35));
				botJButtonsArray[count].addActionListener(new MyButtonListener());
				botShipsPanel.add(botJButtonsArray[count]);
				count++;
			}
		}
	}
	
	// Создаем маленькую панель и отмечаем на ней наши корабли
	private void checkHumanShips() {
		for(Ship ship : humanShipsList) {
			int[] coordinates = ship.getCoordinates();
			for(int coordinate : coordinates) {
				humanJButtonsArray[coordinate].setBackground(Color.GREEN);
			}
		}
	}
	
	private void restart() {
		Service.setIsHit(new ArrayList<Integer>());
		Service.setTmpHits(new ArrayList<Integer>());
		Service.setShots(new boolean[100]);
		Service.setHumanShipCoordinates(new TreeSet());
		Service.setAroundCoordHuman(new ArrayList<Integer>());
		Service.setShipMapHuman(new int[10][10]);
	}
	
	public static void main(String[] args) {
		new Gui().go();
	}
	
		
}
