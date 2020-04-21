package by.aleh.yaskovich.game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class Test {
	
	JFrame frame;
	JLabel scoreLabel;
	JPanel centerPanel;
	JPanel gamePanel;
	JPanel nextBubblesPanel;
	JTextField outgoing;
	
	int score = 42;
	String currentButton;
	String search = "pics\\";
	String search_mini = "pics_mini\\";
	String fileName = "files\\results.txt";
	JButton[] nextBubbles = new JButton[3];
	JButton[] bubbles  = new JButton[81];
	SortedSet<Integer> tmpPathAreaSet = new TreeSet<>();
	ArrayList<String> results = new ArrayList<String>();
	boolean endGame;
	
	public void gui() {
		frame = new JFrame("Bubbles");
		
		// Создаем панель меню (верхнюю)
		JPanel northPanel = new JPanel(new BorderLayout());
			// Создаем панель меню
			JPanel menuPanel = new JPanel();
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new SaveButtonListener());
			JButton openButton = new JButton("Open");
			JButton newGameButton = new JButton("New Game");
			JButton resultsButton = new JButton("Results");
			resultsButton.addActionListener(new ResultsButtonListener());
			JButton rulesButton = new JButton("Rules");
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
			Font font = new Font("Arial", Font.BOLD, 16);
			scoreLabel.setPreferredSize(labelSize);
			scoreLabel.setFont(font);
			scoreLabel.setVerticalAlignment(JLabel.CENTER);
			scoreLabel.setHorizontalAlignment(JLabel.CENTER);
			scorePanel.add(scoreLabel);
		northPanel.add(BorderLayout.EAST, scorePanel);
		
		// Создаем игровую панель
		centerPanel = new JPanel();
			
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
		
		// Создаем нижнюю панель
		JPanel southPanel = new JPanel();
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
	
	public class SaveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			JFileChooser fileSave = new JFileChooser();
			fileSave.showSaveDialog(frame);
			File file = fileSave.getSelectedFile();
			
			
		}
	}
	
	public class ResultsButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
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
			int count = 0;
			for(String s : results) {
				String[] tmp = s.split("-");
				for(int i = 0; i < 3; i++) {
					strArr[count][i] = tmp[i];
				}
				count++;
			}
			
			strArr = sortArr(strArr);
			
			JTextArea resTextArea = new JTextArea(25,50);
			resTextArea.setLineWrap(true);
			resTextArea.setWrapStyleWord(true);
			resTextArea.setEditable(false);
			if(strArr[0][0] == null) {
				resTextArea.setText("Сохраненных результатов пока нет. Вы можете быть первым!");
			} else {
				resTextArea.setText(null);
				for(int i = 0; i < strArr.length; i++) {
					String str = (i+1)+". Очки: "+strArr[i][0]+"; Имя: "+strArr[i][1]+"; Дата: "+strArr[i][2]+"\n";
					resTextArea.append(str);
				}
			}
			JScrollPane qScroller = new JScrollPane(resTextArea);
			qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			resultsPanel.add(qScroller);
			
			centerPanel.add(resultsPanel);
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
	
	public void read() throws FileNotFoundException {
		FileReader reader = new FileReader(fileName);
		try {
			BufferedReader br = new BufferedReader(reader);
			String s;
			while ((s = br.readLine()) != null) {
				if(s.compareTo("") != 0)
				results.add(s);
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String str) throws IOException {
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF8"));
		results.add(str);
		for (int i = 0; i < results.size(); i++) {
			out.write(results.get(i)+"\n");
		}
		results.clear();
		out.close();
	}
	
	private String[][] sortArr(String[][] strArr) {
		String[][] strArrNew = new String[strArr.length][3];
		String[] tmp = new String[3];
		for(int i = 0; i < strArr.length; i++) {
			int scoreTmp = Integer.parseInt(strArr[i][0]);
			for(int j = i+1; j < strArr.length; j++) {
				if(scoreTmp > Integer.parseInt(strArr[j][0])) {
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
		new Test().gui();
	}

}
