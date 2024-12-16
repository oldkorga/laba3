package laba3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")

public class MainFrame extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private Double[] coefficients;

	private JFileChooser fileChooser = null;

	private JMenuItem saveToTextMenuItem;
	private JMenuItem saveToGraphicsMenuItem;
	private JMenuItem saveToCSVMenuItem;
	private JMenuItem searchValueMenuItem;
	private JMenuItem searchPalindromMenuItem;
	private JMenuItem showHelpMenuItem;

	private JTextField textFieldFrom;
	private JTextField textFieldTo;
	private JTextField textFieldStep;

	private Box hBoxResult;

	private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
	private GornerTableModel data;

	protected void saveToGraphicsFile(File selectedFile) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
			for (int i = 0; i < data.getRowCount(); i++) {
				out.writeDouble((Double) data.getValueAt(i, 0));
				out.writeDouble((Double) data.getValueAt(i, 1));
			}
			out.close();
		} catch (Exception e) {}
	}

	protected void saveToTextFile(File selectedFile) {
		try {
			PrintStream out = new PrintStream(selectedFile);
			out.println("Результаты табулирования многочлена по схемеГорнера");
			out.print("Многочлен: ");
			for (int i = 0; i < coefficients.length; i++) {
				out.print(coefficients[i] + "*X^" + (coefficients.length - i - 1));
				if (i != coefficients.length - 1)
					out.print(" + ");
			}
			out.println("");
			out.println("Интервал от " + data.getFrom() + " до " + data.getTo() +
					" с шагом " + data.getStep());
			out.println("====================================================");
			for (int i = 0; i < data.getRowCount(); i++) {
				out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
			}
			out.close();
		} catch (FileNotFoundException e) {}
	}

	protected void saveToCSVFile(File selectedFile) {
		try {
			PrintStream out = new PrintStream(selectedFile);
			for(int i = 0; i < data.getColumnCount(); i++) {
				out.print(data.getColumnName(i));
				out.print(",");
			}
			out.println();
			for(int i = 0; i < data.getRowCount(); i++) {
				for(int j = 0; j < data.getColumnCount(); j++) {
					out.print(data.getValueAt(i, j));
					out.print(",");
				}
				out.println();
			}
			out.close();
		} catch (FileNotFoundException e) {}
	}
	
	private Action createSaveAction(String name, int type) {
		Action saveAction = new AbstractAction(name) {
			public void actionPerformed(ActionEvent event) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					File f = fileChooser.getSelectedFile();
					String filePath = f.getPath();
					switch (type) {
					default:
					case 0:
						f = new File(filePath + ".txt");
						saveToTextFile(f);
						break;
					case 1:
						f = new File(filePath + ".bin");
						saveToGraphicsFile(f);
						break;
					case 2:
						f = new File(filePath + ".csv");
						saveToCSVFile(f);
						break;
					}
				}
			}
		};
		return saveAction;
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Файл");
		menuBar.add(fileMenu);
		JMenu tableMenu = new JMenu("Таблица");
		menuBar.add(tableMenu);
		JMenu saveMenu = new JMenu("Сохранить");
		fileMenu.add(saveMenu);
		JMenu helpMenu = new JMenu("Справка");
		menuBar.add(helpMenu);

		saveToTextMenuItem = saveMenu.add(createSaveAction("Сохранить в текстовый файл", 0));
		saveToTextMenuItem.setEnabled(false);

		saveToGraphicsMenuItem = saveMenu.add(createSaveAction("Сохранить данные для построения графика", 1));
		saveToGraphicsMenuItem.setEnabled(false);
		
		saveToCSVMenuItem = saveMenu.add(createSaveAction("Сохранить в CSV файл", 2));
		saveToCSVMenuItem.setEnabled(false);

		Action searchValueAction = new AbstractAction("Найти значение многочлена") {
		    public void actionPerformed(ActionEvent event) {
		        String value = JOptionPane.showInputDialog(MainFrame.this, 
		                "Введите значение для поиска", 
		                "Поиск значения", JOptionPane.QUESTION_MESSAGE);
		        if (value != null) {
		            try {
		                // Проверяем, является ли введенное значение числом
		                Double.parseDouble(value); // Если не число, вызовет NumberFormatException
		                renderer.setNeedle(value);
		                getContentPane().repaint();
		            } catch (NumberFormatException e) {
		                // Показываем сообщение об ошибке
		                JOptionPane.showMessageDialog(MainFrame.this, 
		                        "Ошибка: введите корректное число с плавающей точкой.", 
		                        "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
		            }
		        }
		    }
		};

		
		
		JCheckBoxMenuItem palindromeCheckBox = new JCheckBoxMenuItem("Проверять на палиндромы");
        palindromeCheckBox.setSelected(false);
        palindromeCheckBox.addActionListener(e -> {
            renderer.setPalindromeCheckEnabled(palindromeCheckBox.isSelected());
            getContentPane().repaint(); // Перерисовываем таблицу
        });

		// Добавляем флажок в интерфейс
        searchPalindromMenuItem = tableMenu.add(palindromeCheckBox);


		searchValueMenuItem = tableMenu.add(searchValueAction);
		
		searchValueMenuItem.setEnabled(false);
		
		Action showHelpAction = new AbstractAction("О программе") {
			public void actionPerformed(ActionEvent ev) {
				ImageIcon icon = new ImageIcon("C:/Users/Dirov/eclipse-workspace/laba3/src/StudentPhoto.jpg");
				JOptionPane.showConfirmDialog(MainFrame.this, "Дировский 6 группа", 
						"about", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
			}
		};
		showHelpMenuItem = helpMenu.add(showHelpAction);
		showHelpMenuItem.setEnabled(true);

	}

	private void initButtons() {
		JButton buttonCalc = new JButton("Вычислить");
		buttonCalc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					Double from = Double.parseDouble(textFieldFrom.getText());
					Double to = Double.parseDouble(textFieldTo.getText());
					Double step = Double.parseDouble(textFieldStep.getText());
					data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
					JTable table = new JTable(data);
					table.setDefaultRenderer(Double.class, renderer);
					table.setRowHeight(30);
					hBoxResult.removeAll();
					hBoxResult.add(new JScrollPane(table));
					saveToTextMenuItem.setEnabled(true);
					saveToGraphicsMenuItem.setEnabled(true);
					saveToCSVMenuItem.setEnabled(true);
					searchValueMenuItem.setEnabled(true);
					getContentPane().validate();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой",
							"Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		JButton buttonReset = new JButton("Очистить поля");
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				textFieldFrom.setText("0.0");
				textFieldTo.setText("1.0");
				textFieldStep.setText("0.1");
				hBoxResult.removeAll();
				hBoxResult.add(new JPanel());
				saveToTextMenuItem.setEnabled(false);
				saveToGraphicsMenuItem.setEnabled(false);
				searchValueMenuItem.setEnabled(false);
				renderer.setNeedle(null);
			
				getContentPane().validate();
			}
		});
		
		// Кнопка поиска(флажок)
	    //JButton searchButton = new JButton("Поиск");
	    //searchButton.addActionListener(e -> {
	      //  String inputValue = JOptionPane.showInputDialog("Введите значение для поиска:");
	       // if (inputValue != null) {
	         //   renderer.setNeedle(inputValue); 
	           // getContentPane().repaint();
	       // }
	    //});
		
		
		Box hboxButtons = Box.createHorizontalBox();
		hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
		hboxButtons.add(Box.createHorizontalGlue());
		hboxButtons.add(buttonCalc);
		hboxButtons.add(Box.createHorizontalStrut(30));
		hboxButtons.add(buttonReset);
		hboxButtons.add(Box.createHorizontalStrut(30));
		hboxButtons.add(Box.createHorizontalGlue());
		hboxButtons.setPreferredSize(new Dimension((int) (hboxButtons.getMaximumSize().getWidth()),
				(int) (hboxButtons.getMinimumSize().getHeight()) * 2));
		getContentPane().add(hboxButtons, BorderLayout.SOUTH);
	}
	
	private void addRangeBox() {
		JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
		textFieldFrom = new JTextField("0.0", 10);
		textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());

		JLabel labelForTo = new JLabel("до:");
		textFieldTo = new JTextField("1.0", 10);
		textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
		JLabel labelForStep = new JLabel("с шагом:");
		textFieldStep = new JTextField("0.1", 10);
		textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
		
		Box hboxRange = Box.createHorizontalBox();

		hboxRange.setBorder(BorderFactory.createBevelBorder(1));
		hboxRange.add(Box.createHorizontalGlue());
		hboxRange.add(labelForFrom);
		hboxRange.add(Box.createHorizontalStrut(10));
		hboxRange.add(textFieldFrom);
		hboxRange.add(Box.createHorizontalStrut(20));
		hboxRange.add(labelForTo);
		hboxRange.add(Box.createHorizontalStrut(10));
		hboxRange.add(textFieldTo);
		hboxRange.add(Box.createHorizontalStrut(20));
		hboxRange.add(labelForStep);
		hboxRange.add(Box.createHorizontalStrut(10));
		hboxRange.add(textFieldStep);
		hboxRange.add(Box.createHorizontalGlue());
		hboxRange.setPreferredSize(new Dimension((int) (hboxRange.getMaximumSize().getWidth()),
				(int) (hboxRange.getMinimumSize().getHeight()) * 2));
		getContentPane().add(hboxRange, BorderLayout.NORTH);
	}

	public MainFrame(Double[] coefficients) {
		super("Табулирование многочлена на отрезке по схеме Горнера");
		this.coefficients = coefficients;
		

		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - WIDTH) /2, (kit.getScreenSize().height - HEIGHT) / 2);

		initMenu();
		
		addRangeBox();
		
		initButtons();
		
		hBoxResult = Box.createHorizontalBox();
		hBoxResult.add(new JPanel());
		getContentPane().add(hBoxResult, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
			System.exit(-1);
		}
		Double[] coefficients = new Double[args.length];
		int i = 0;
		try {
			for (String arg : args) {
				coefficients[i++] = Double.parseDouble(arg);
			}
		} catch (NumberFormatException ex) {
			System.out.println("Ошибка преобразования строки '" + args[i] + "' в число типа Double");
			System.exit(-2);
		}
		new MainFrame(coefficients);
	}
}

