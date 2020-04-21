package by.aleh.yaskovich.game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class Game {
	
	int score;
	String currentButton;
	String search = "pics\\";
	String search_mini = "pics_mini\\";
	String fileName = "files\\results.txt";
	JButton[] nextBubbles = new JButton[3];
	JButton[] bubbles  = new JButton[81];
	SortedSet<Integer> tmpPathAreaSet = new TreeSet<>();
	ArrayList<String> results = new ArrayList<String>();
	boolean endGame;
	
	JFrame frame;
	JPanel northPanel;
	JLabel scoreLabel;
	JPanel centerPanel;
	JPanel gamePanel;
	JTextField outgoing;
	JPanel southPanel;
	JPanel nextBubblesPanel;
	Font font = new Font("Arial", Font.BOLD, 16);
	
	public void gui() {
		frame = new JFrame("Bubbles");
		
		// Создаем панель меню (верхнюю)
		northPanel = new JPanel(new BorderLayout());
		// Создаем панель меню
			JPanel menuPanel = new JPanel();
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new SaveButtonListener());
			JButton openButton = new JButton("Open");
			openButton.addActionListener(new OpenButtonListener());
			JButton newGameButton = new JButton("New Game");
			newGameButton.addActionListener(new NewGameButtonListener());
			JButton resultsButton = new JButton("Results");
			resultsButton.addActionListener(new ResultsButtonListener());
			JButton rulesButton = new JButton("Rules");
			rulesButton.addActionListener(new RulesButtonListener());
			menuPanel.add(saveButton);
			menuPanel.add(openButton);
			menuPanel.add(newGameButton);
			menuPanel.add(resultsButton);
			menuPanel.add(rulesButton);
		northPanel.add(BorderLayout.WEST, menuPanel);
			// Создаем панель подсчета очков
			JPanel scorePanel = new JPanel();
			scoreLabel = new JLabel("Score: "+ score);
			Dimension labelSize = new Dimension(110, 30);
			scoreLabel.setPreferredSize(labelSize);
			scoreLabel.setFont(font);
			scoreLabel.setVerticalAlignment(JLabel.CENTER);
			scoreLabel.setHorizontalAlignment(JLabel.CENTER);
			scorePanel.add(scoreLabel);
		northPanel.add(BorderLayout.EAST, scorePanel);
		
		// Создаем игровую панель
		centerPanel = new JPanel();
			gamePanel = new JPanel(new GridLayout(9,9));
			gamePanel.setBorder(BorderFactory.createEmptyBorder(20,50,15,50));
			bubbles = createBubbles(search);
			for(int i = 0; i < bubbles.length; i++) {
				bubbles[i].setPreferredSize(new Dimension(50, 50));
				gamePanel.add(bubbles[i]);
			}
		centerPanel.add(gamePanel);
		
		// Создаем нижнюю панель
		southPanel = new JPanel();
			southPanel.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
			nextBubblesPanel = new JPanel(new GridLayout(1,3));
			nextBubbles = createNextBubbles(search_mini);
			for(int i = 0; i < nextBubbles.length; i++) {
				nextBubblesPanel.add(nextBubbles[i]);
			}
		southPanel.add(nextBubblesPanel);
		
		frame.getContentPane().add(BorderLayout.NORTH, northPanel);
		frame.getContentPane().add(BorderLayout.CENTER, centerPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(20, 20);
		frame.pack();
		frame.setVisible(true);
	}
	
	public class MyButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			Object source = event.getSource();
			JButton button = ((JButton)source);
			Icon icon = button.getIcon();
			
			if(icon != null) {
				if(currentButton != null) {
					tmpPathAreaSet.clear();
					int element = Integer.parseInt(currentButton);
					bubbles[element].setBackground(null);
				}
				button.setBackground(Color.white);
				currentButton = button.getName();
				pathArea(currentButton);
			}
			
			if(icon == null && currentButton != null) {

				int tmpCurBut = Integer.parseInt(button.getName());
				if(tmpPathAreaSet.contains(tmpCurBut)) {
					
					tmpPathAreaSet.clear();
					int currentButtonInt = Integer.parseInt(currentButton);
					icon = bubbles[currentButtonInt].getIcon();
					bubbles[currentButtonInt].setIcon(null);
					bubbles[currentButtonInt].setBackground(null);
					button.setIcon(icon);
					
					boolean check = checkLanes();
					if(!check) {
						// Добавляем три новых шарика на поле
						addNewBubbles(nextBubbles, search);
						// Меняем шарики случайного цвета в нижней панели
						nextBubblesPanel.removeAll();
						nextBubbles = createNextBubbles(search_mini);
						for(int i = 0; i < 3; i++) {
							nextBubblesPanel.add(nextBubbles[i]);
						}
					}	
					check = checkLanes();
					if(check) {
						// Добавляем три новых шарика на поле
						addNewBubbles(nextBubbles, search);
						// Меняем шарики случайного цвета в нижней панели
						nextBubblesPanel.removeAll();
						nextBubbles = createNextBubbles(search_mini);
						for(int i = 0; i < 3; i++) {
							nextBubblesPanel.add(nextBubbles[i]);
						}
					}
					
					currentButton = null;
					if(endGame) { addResult(); }
				} 
				
			}
		}
	}
	
	public class SaveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			int[] pathBubbles = new int[82];
			for(int i = 0; i < 81; i++) {
				if(bubbles[i].getIcon() != null) {
					char ch = bubbles[i].getIcon().toString().charAt(5);
					pathBubbles[i] = Integer.parseInt(ch+"");
				}
			}
			pathBubbles[81] = score;
			
			JFileChooser fileSave = new JFileChooser();
			fileSave.showSaveDialog(frame);
			File file = fileSave.getSelectedFile();
			
			try {
				FileOutputStream fileStream = new FileOutputStream(file);
				ObjectOutputStream os = new ObjectOutputStream(fileStream);
				os.writeObject(pathBubbles);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class OpenButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {		
			
			int[] pathBubbles = new int[82];
			JFileChooser fileOpen = new JFileChooser();
			fileOpen.showOpenDialog(frame);
			File file = fileOpen.getSelectedFile();
			if(file != null) {
				try {
					FileInputStream fileIn = new FileInputStream(file);
					ObjectInputStream is = new ObjectInputStream(fileIn);
					pathBubbles = (int[]) is.readObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				score = pathBubbles[81];
				scoreLabel.setText("Score: "+ score);
				for(int i = 0; i < 81; i++) {
					JButton button = new JButton();
					button.setName(i+"");
					button.setPreferredSize(new Dimension(50, 50));
					button.addActionListener(new MyButtonListener());
					if(pathBubbles[i] > 0) {
						button.setIcon(new ImageIcon(search+pathBubbles[i]+".png"));
					}
					bubbles[i] = button;
				}
				centerPanel.removeAll();
				gamePanel.removeAll();
				for(int i = 0; i < bubbles.length; i++) {
					bubbles[i].setPreferredSize(new Dimension(50, 50));
					gamePanel.add(bubbles[i]);
				}
				centerPanel.add(gamePanel);
				frame.pack();
			}
		}
	}
	
	public class NewGameButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			score = 0;
			scoreLabel.setText("Score: "+ score);
			
			centerPanel.removeAll();
			gamePanel = new JPanel(new GridLayout(9,9));
			gamePanel.setBorder(BorderFactory.createEmptyBorder(20,50,15,50));
			bubbles = createBubbles(search);
			for(int i = 0; i < 81; i++) {
				gamePanel.add(bubbles[i]);
			}
			centerPanel.add(gamePanel);
			
			southPanel.remove(nextBubblesPanel);
			nextBubblesPanel = new JPanel(new GridLayout(1,3));
			nextBubbles = createNextBubbles(search_mini);
			for(int i = 0; i < nextBubbles.length; i++) {
				nextBubblesPanel.add(nextBubbles[i]);
			}
			southPanel.add(nextBubblesPanel);
			
	        frame.pack();

		}
	}
	
	public class ResultsButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			showResults();
		}
	}
	
	public class RulesButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			centerPanel.removeAll();
			gamePanel.removeAll();
			gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
			gamePanel.setBorder(BorderFactory.createEmptyBorder(20,10,15,0));
			gamePanel.setPreferredSize(new Dimension(550, 510));
			
			try {
				FileReader reader = new FileReader("files\\rules.txt");
				BufferedReader br = new BufferedReader(reader);
				String s;
				while ((s = br.readLine()) != null) {
					gamePanel.add(new JLabel(s+"\n"));
				}
				reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			JPanel returnPanel = new JPanel();
			returnPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
			JButton returnButton = new JButton("Вернуться к игре");
			returnButton.addActionListener(new ReturnButtonListener());
			returnPanel.add(returnButton);
			gamePanel.add(returnPanel);
			
			centerPanel.add(gamePanel);
			
			frame.pack();
		}
	}
	
	public class ReturnButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			centerPanel.remove(gamePanel);
			gamePanel = new JPanel(new GridLayout(9,9));
			gamePanel.setBorder(BorderFactory.createEmptyBorder(20,50,15,50));
			for(int i = 0; i < bubbles.length; i++) {
				bubbles[i].setPreferredSize(new Dimension(50, 50));
				gamePanel.add(bubbles[i]);
			}
			centerPanel.add(gamePanel);
			
			frame.pack();
		}
	}
	
	public class SaveResButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String name = outgoing.getText().trim();
			if(name.equals("")) {
				name = "Unnamed";
			}
			outgoing.setText("");
			Date dateNow = new Date();
		    SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
		    String str = score+"-"+name+"-"+formatForDateNow.format(dateNow);
		    
			try {
				read();
				write(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			showResults();
		}
	}
	
	// Создаем массив из трех шариков случайного цвета
	private JButton[] createNextBubbles(String search_mini) {
		JButton[] nextBubbles = new JButton[3];
		for(int i = 0; i < nextBubbles.length; i++) {
			int rand = (int)(Math.random()*7+1);
			JButton button = new JButton(new ImageIcon(search_mini+rand+".png"));
			button.setPreferredSize(new Dimension(35, 35));
			button.setName(""+rand);
			nextBubbles[i] = button;
		}
		return nextBubbles;
	}
	
	
	// Создаем стартовую позицию на игровом поле
	private JButton[] createBubbles(String search) {
		JButton[] bubbles = new JButton[81];
		for(int i = 0; i < 81; i++) {
			JButton button = new JButton();
			button.setName(i+"");
			button.setPreferredSize(new Dimension(50, 50));
			button.addActionListener(new MyButtonListener());
			bubbles[i] = button;
		}
		int count = 0;
		while(count < 3) {
			int rand = (int)(Math.random()*81);
			if(bubbles[rand].getIcon() == null) {
				int randomColor = (int)(Math.random()*7+1);
				bubbles[rand].setIcon(new ImageIcon(search+randomColor+".png"));
				count++;
			}
		}
		return bubbles;
	}
	
	
	// Добавляем три новых шарика на поле
	private void addNewBubbles(JButton[] nextBubbles, String search) {
		String str;
		String[] strArr = new String[nextBubbles.length];
		for(int i = 0; i < nextBubbles.length; i++) {
			str = nextBubbles[i].getIcon().toString();
			char ch = str.charAt(10);
			strArr[i] = search+ch+".png";
		}
		
		int freePositions = 0;
		for(JButton b : bubbles) {
			if(b.getIcon() == null) { freePositions++; }
		}
		
		int count = 0;
		while(count < nextBubbles.length) {
			int rand = (int)(Math.random()*81);
			if(bubbles[rand].getIcon() == null) {
				bubbles[rand].setIcon(new ImageIcon(strArr[count]));
				count++;
				freePositions--;
			}
			if(freePositions == 0) { 
				endGame = true;
				break; 
			}
			
		}		
		gamePanel.removeAll();
		for(int i = 0; i < 81; i++) {
			gamePanel.add(bubbles[i]);
		}
	}
	
	
	// Проверяем, есть ли 5+ шариков одного цвета в ряд или по диагонали
	private boolean checkLanes() {
		boolean res = false;
		// проверка по горизонтали
		for(int i = 0; i <= 72; i = i+9) {
			for(int j = i; j <= i+4; j++) {
				if(bubbles[j].getIcon() != null) {
					ArrayList<String> resultsTmpHorizontal = new ArrayList<String>();
					int countHorizontal = j+1;
					boolean tmp = true;
					resultsTmpHorizontal.add(bubbles[j].getName());
					while(tmp && countHorizontal <= 80) {
						if(bubbles[countHorizontal].getIcon() != null) {
							String str1Horizontal = bubbles[j].getIcon().toString();
							String str2Horizontal = bubbles[countHorizontal].getIcon().toString();
							if(str1Horizontal.equals(str2Horizontal)) {
								resultsTmpHorizontal.add(bubbles[countHorizontal].getName());
								countHorizontal++;
							} else {
								tmp = false;
							}
						} else {
							tmp = false;
						}
					}
					if(resultsTmpHorizontal.size() >= 5) {
						changeScore(resultsTmpHorizontal.size());
						for(int k = 0; k < resultsTmpHorizontal.size(); k++) {
							for(String s : resultsTmpHorizontal) {
								int element = Integer.parseInt(s);
								bubbles[element].setIcon(null);
							}
							res = true;
						}
					} else {
						resultsTmpHorizontal.clear();
					}
				}
			}
		}
		
		// проверка по вертикали
		for(int i = 0; i <= 44; i++) {
			if(bubbles[i].getIcon() != null) {
				ArrayList<String> resultsTmpVertical = new ArrayList<String>();
				int countVertical = i+9;
				boolean tmp = true;
				resultsTmpVertical.add(bubbles[i].getName());
				while(tmp && countVertical <= 80) {
					if(bubbles[countVertical].getIcon() != null) {
						String str1Vertical = bubbles[i].getIcon().toString();
						String str2Vertical = bubbles[countVertical].getIcon().toString();
						if(str1Vertical.equals(str2Vertical)) {
							resultsTmpVertical.add(bubbles[countVertical].getName());
							countVertical = countVertical+9;
						} else {
							tmp = false;
						}
					} else {
						tmp = false;
					}
				}
				if(resultsTmpVertical.size() >= 5) {
					changeScore(resultsTmpVertical.size());
					for(int k = 0; k < resultsTmpVertical.size(); k++) {
						for(String s : resultsTmpVertical) {
							int element = Integer.parseInt(s);
							bubbles[element].setIcon(null);
						}
						res = true;
					}
				} else {
					resultsTmpVertical.clear();
				}
			}
		}
		
		// Проверка по диагонали №1
		for(int i = 0; i <= 80; i++) {
			if(bubbles[i].getIcon() != null) {
				ArrayList<String> resultsTmpDiagonal1 = new ArrayList<String>();
				int countDiagonal1 = i+10;
				boolean tmp = true;
				resultsTmpDiagonal1.add(bubbles[i].getName());
				while(tmp && countDiagonal1 <= 80) {
					if(bubbles[countDiagonal1].getIcon() != null) {
						String str1Diagonal1 = bubbles[i].getIcon().toString();
						String str2Diagonal1 = bubbles[countDiagonal1].getIcon().toString();
						if(str1Diagonal1.equals(str2Diagonal1)) {
							resultsTmpDiagonal1.add(bubbles[countDiagonal1].getName());
							countDiagonal1 = countDiagonal1+10;
						} else {
							tmp = false;
						}
					} else {
						tmp = false;
					}
				}
				if(resultsTmpDiagonal1.size() >= 5) {
					changeScore(resultsTmpDiagonal1.size());
					for(int k = 0; k < resultsTmpDiagonal1.size(); k++) {
						for(String s : resultsTmpDiagonal1) {
							int element = Integer.parseInt(s);
							bubbles[element].setIcon(null);
						}
						res = true;
					}
				} else {
					resultsTmpDiagonal1.clear();
				}
			}
		}
		
		// Проверка по диагонали №2
		for(int i = 0; i <= 80; i++) {
			if(bubbles[i].getIcon() != null) {
				ArrayList<String> resultsTmpDiagonal2 = new ArrayList<String>();
				int countDiagonal2 = i+8;
				boolean tmp = true;
				resultsTmpDiagonal2.add(bubbles[i].getName());
				while(tmp && countDiagonal2 <= 80) {
					if(bubbles[countDiagonal2].getIcon() != null) {
						String str1Diagonal2 = bubbles[i].getIcon().toString();
						String str2Diagonal2 = bubbles[countDiagonal2].getIcon().toString();
						if(str1Diagonal2.equals(str2Diagonal2)) {
							resultsTmpDiagonal2.add(bubbles[countDiagonal2].getName());
							countDiagonal2 = countDiagonal2+8;
						} else {
							tmp = false;
						}
					} else {
						tmp = false;
					}
				}
				if(resultsTmpDiagonal2.size() >= 5) {
					changeScore(resultsTmpDiagonal2.size());
					for(int k = 0; k < resultsTmpDiagonal2.size(); k++) {
						for(String s : resultsTmpDiagonal2) {
							int element = Integer.parseInt(s);
							bubbles[element].setIcon(null);
						}
						res = true;
					}
				} else {
					resultsTmpDiagonal2.clear();
				}
			}
		}
		
		return res;
	} // Конец checkLanes()
	
	
	// Изменяем количество очков
	private void changeScore(int arrSize) {
		if(arrSize == 5) {
			score = score + 5;
		} else {
			score = score + 5 + (arrSize - 5)*3 - 1;
		}
		scoreLabel.setText("Score: "+ score);
	}
	
	// Проверить путь
	private void pathArea(String s) {
		int start = Integer.parseInt(s);
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		tmp.addAll(checkNext(start));
		tmpPathAreaSet.addAll(tmp);
		int count = tmp.size();
		
		while(count > 0) {
			for(int i = 0; i < count; i++) {
				start = tmp.get(i);
				tmp.addAll(checkNext(start));
			}
			
			count = 0;
			while(count < tmp.size()) {
				if(tmpPathAreaSet.contains(tmp.get(count))) {
					tmp.remove(count);
				} else {
					tmpPathAreaSet.add(tmp.get(count));
					count++;
				}
			}
			count = tmp.size();
		}
            
	}
	
	
	// Вспомогательный метод для pathArea()
	private ArrayList<Integer> checkNext(int start) {
		ArrayList<Integer> checkNext = new ArrayList<Integer>();
		
		if(start-9 >= 0) {
			if(bubbles[start-9].getIcon() == null) { checkNext.add(start-9); }
		}
		
		if(start+1 <= 80 && (start+1)/9 == start/9) {
			if(bubbles[start+1].getIcon() == null) { checkNext.add(start+1); }
		}
		
		if(start+9 <= 80) {
			if(bubbles[start+9].getIcon() == null) { checkNext.add(start+9); }
		}
		
		if(start-1 >= 0 && (start-1)/9 == start/9) {
			if(bubbles[start-1].getIcon() == null) { checkNext.add(start-1); }
		}
		
		return checkNext;
	}

	// Выводим панель для записи результата в файл
	private void addResult() {
		centerPanel.removeAll();
		
		gamePanel = new JPanel();
		gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
		gamePanel.setPreferredSize(new Dimension(550, 485));
		
		JPanel congratulationPanel = new JPanel();
		congratulationPanel.setBorder(BorderFactory.createEmptyBorder(100,0,0,0));
		congratulationPanel.setPreferredSize(new Dimension(450, 40));
			JLabel congratulationLabel = new JLabel("Игра окончена. Вы набрали "+score+" очка(ов). Поздравляем!");
			congratulationLabel.setFont(font);
			congratulationPanel.add(congratulationLabel);
		JPanel sendPanel = new JPanel();
		sendPanel.setBorder(BorderFactory.createEmptyBorder(40,0,0,0));
		sendPanel.setPreferredSize(new Dimension(450, 240));
			JPanel sendNamePanel = new JPanel();
				JLabel nameLabel = new JLabel("Ваше имя: ");
				outgoing = new JTextField(30);
			sendNamePanel.add(nameLabel);
			sendNamePanel.add(outgoing);
			JPanel saveResultsPanel = new JPanel();
			saveResultsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
				JButton saveResultsButton = new JButton("Сохранить результат");
				saveResultsButton.addActionListener(new SaveResButtonListener());
			saveResultsPanel.add(saveResultsButton);
		sendPanel.add(sendNamePanel);
		sendPanel.add(saveResultsPanel);
		
		gamePanel.add(congratulationPanel);
		gamePanel.add(sendPanel);
		
		centerPanel.add(gamePanel);
	}
	
	// Показать результаты
	private void showResults() {
		results.clear();
		centerPanel.removeAll();
		
		JPanel resultsPanel = new JPanel();
		resultsPanel.setPreferredSize(new Dimension(550, 485));
		resultsPanel.setBorder(BorderFactory.createEmptyBorder(20,50,15,50));
		try {
			read();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[][] strArr = new String[results.size()][3];
		if(!results.isEmpty()) {
			int count = 0;
			for(int i = 0; i < results.size(); i++) {
				String[] tmp = results.get(i).split("-");
				
				for(int j = 0; j < 3; j++) {
					strArr[count][j] = tmp[j];
				} 
				count++;
			}
			strArr = sortArr(strArr);
		}
		
		JTextArea resTextArea = new JTextArea(25,50);
		resTextArea.setLineWrap(true);
		resTextArea.setWrapStyleWord(true);
		resTextArea.setEditable(false);
		resTextArea.setBackground(null);
		
		if(results.isEmpty()) {
			resTextArea.setText("Сохраненных результатов пока нет. Вы можете быть первым!");
		} else {
			resTextArea.setText(null);
			for(int i = 0; i < strArr.length; i++) {
				String str = (i+1)+". Очки: "+strArr[i][0]+"; Имя: "+strArr[i][1]+"; Дата: "+strArr[i][2]+"\n";
				resTextArea.append(str);
			}
		} 
		JScrollPane qScroller = new JScrollPane(resTextArea);
		qScroller.setBorder(null);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		resultsPanel.add(qScroller);
		
		centerPanel.add(resultsPanel);
		frame.pack();
		endGame = false;
	}
	
	// Чтение результатов из файла results.txt
 	public void read() throws FileNotFoundException {
 		results.clear();
		FileReader reader = new FileReader(fileName);
		try {
			BufferedReader br = new BufferedReader(reader);
			String s;
			while ((s = br.readLine()) != null) {
				results.add(s);
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Запись результата в файл results.txt
	public void write(String str) throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		results.add(str);
		for (int i = 0; i < results.size(); i++) {
			writer.write(results.get(i)+"\n");
		}
		results.clear();
		writer.close();
	}
	
	// Cортируем массив результатов по к-ву набранных очков
	private String[][] sortArr(String[][] strArr) {
		String[][] strArrNew = new String[strArr.length][3];
		String[] tmp = new String[3];
		for(int i = 0; i < strArr.length; i++) {
			int scoreTmp = Integer.parseInt(strArr[i][0]);
			for(int j = i+1; j < strArr.length; j++) {
				if(scoreTmp < Integer.parseInt(strArr[j][0])) {
					scoreTmp = Integer.parseInt(strArr[j][0]);
					tmp[0] = strArr[i][0];
					tmp[1] = strArr[i][1];
					tmp[2] = strArr[i][2];
					
					strArr[i][0] = strArr[j][0];
					strArr[i][1] = strArr[j][1];
					strArr[i][2] = strArr[j][2];
					
					strArr[j][0] = tmp[0];
					strArr[j][1] = tmp[1];
					strArr[j][2] = tmp[2];
				}
			}
		}
		return strArr;
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.gui();
	}

}
