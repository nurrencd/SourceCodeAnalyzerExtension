import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import design.team.nothing.AnalyzerChain;
import design.team.nothing.Data;
import design.team.nothing.Preprocessor;
import javafx.scene.control.RadioButton;

public class GUIApp {
	
	public static final List<String> TEXTAREACOMPONENTS = Collections.unmodifiableList(Arrays.asList(
			"classlist", "exclude", "filters", "pattern", "resolutionstrategy", "algorithms", "depth"
			));
	
	public static final List<String> FILECOMPONENTS = Collections.unmodifiableList(Arrays.asList(
			"path", "main"
			));
	
	public static final List<String> BUTTONCOMPONENTS = Collections.unmodifiableList(Arrays.asList(
			"uml", "recursive", "sequence", "java", "synthetic"
			));
	
	
	
	
	private Map<String, Prompt> promptMap;
	private FileManager m;
	private Compiler c;
	private JFrame frame;
	private String currentFile;

	private Map<String, JTextArea> compMap = new HashMap<>();
	private Map<String, JCheckBox> buttonMap = new HashMap<>();

	public GUIApp(FileManager m, Compiler c) {
		this.promptMap = new HashMap<String, Prompt>();
		this.promptMap.put("save", new FileSystemPrompt(JFileChooser.DIRECTORIES_ONLY));
		this.promptMap.put("load", new FileSystemPrompt(JFileChooser.FILES_ONLY));
		this.promptMap.put("path", new FileSystemPrompt(JFileChooser.DIRECTORIES_ONLY));
		this.promptMap.put("main", new FileSystemPrompt(JFileChooser.FILES_ONLY));
		this.m = m;
		this.c = c;
	}

	public void start() {
		this.frame = new JFrame();
		this.frame.setSize(new Dimension(800, 800));
		this.frame.getContentPane().setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS));

		this.frame.add(this.createTopPanel());
		this.frame.add(this.createMiddlePanel());
		this.frame.add(this.createBottomPanel());

		this.frame.validate();
		this.frame.setVisible(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private JComponent createTopPanel() {
		JComponent panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 100));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton loadButton = new JButton("Load Properties");
		panel.add(loadButton);
		loadButton.addActionListener((e) -> {
			Map<String, String> map;
			String response = this.promptMap.get("load").getFilePath("Enter the Properties File to load: ");
			if (response != null) {
				System.out.println(response);
				map = this.m.load(response);
				for (JCheckBox box : this.buttonMap.values()) {
					box.setSelected(false);
				}
				for (String key : map.keySet()) {
					System.out.println(key);
					if (buttonMap.containsKey(key)) {
						buttonMap.get(key).setSelected(true);
					}
					if (!compMap.containsKey(key)) {
						continue;
					}
					System.out.println(key);
					this.compMap.get(key).setText(map.get(key));
				}
			}
		});

		JButton saveButton = new JButton("Save Properties");
		panel.add(saveButton);
		saveButton.addActionListener((e) -> {
			String filepath = this.promptMap.get("save").getFilePath("");
			String response = this.promptMap.get("save").getString("Enter save destination");
			if (response != null) {
				System.out.println(filepath + "/" + response);
				Map<String, String> map = new HashMap<String, String>();
				for (String key : this.compMap.keySet()) {
					map.put(key, this.compMap.get(key).getText());
				}
				for (String key : this.buttonMap.keySet()) {
					if (this.buttonMap.get(key).isSelected()) {
						map.put(key, "true");
					}
				}
				this.m.save(filepath + "/" + response, this.c.compile(map));
			}
		});

		JButton newButton = new JButton("New Properties");
		newButton.addActionListener((e) -> {
			this.compMap.values().forEach(t -> t.setText(""));
		});
		panel.add(newButton);

		// format
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	}

	private JComponent createMiddlePanel() {

		JComponent panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		for (String name : this.FILECOMPONENTS) {
			int function = JFileChooser.FILES_ONLY;
			if (name.equals("path")) {
				function = JFileChooser.DIRECTORIES_ONLY;
			}
			panel.add(createPromptPanel(name, function));
			panel.add(Box.createRigidArea(new Dimension(5, 10)));
		}
		
		for (String name : this.TEXTAREACOMPONENTS) {
			panel.add(createPropertyPanel(name));
			panel.add(Box.createRigidArea(new Dimension(5, 10)));
		}
		
		for (String name : this.BUTTONCOMPONENTS) {
			panel.add(createButton(name));
			panel.add(Box.createRigidArea(new Dimension(5, 10)));
		}

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;

	}
	
	private JComponent createButton(String title) {
		JCheckBox button = new JCheckBox(title);
		this.buttonMap.put(title, button);
		return button;
	}

	private JComponent createPromptPanel(String title, int type) {
		JComponent mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(800, 50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		JLabel mainLabel = new JLabel(title + ": ");
		JTextArea mainPath = new JTextArea();
		JButton mainButton = new JButton(" ");
		compMap.put(title.toLowerCase(), mainPath);
		mainButton.addActionListener((e) -> {
			String path = this.promptMap.get(title).getFilePath("Select the " + title + " to use.");
			if (path != null) {
				compMap.get(title).setText(path);
			}
		});

		mainPanel.add(mainLabel);
		mainPanel.add(mainPath);
		mainPanel.add(mainButton);
		return mainPanel;
	}

	private JComponent createPropertyPanel(String tag) {
		JComponent exclusionsPanel = new JPanel();
		exclusionsPanel.setPreferredSize(new Dimension(800, 100));
		exclusionsPanel.setLayout(new BoxLayout(exclusionsPanel, BoxLayout.X_AXIS));
		exclusionsPanel.add(new JLabel(tag + ": "));
		JTextArea exclusionsArea = new JTextArea();
		compMap.put(tag.toLowerCase(), exclusionsArea);
		exclusionsArea.setEditable(true);
		exclusionsPanel.add(exclusionsArea);
		return exclusionsPanel;
	}

	private JComponent createBottomPanel() {
		JComponent panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 100));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton runButton = new JButton("Run");
		runButton.addActionListener((a) -> {
			Map<String, String> map = new HashMap<String, String>();
			for (String key : this.compMap.keySet()) {
				map.put(key, this.compMap.get(key).getText());
			}
			for (String key : this.buttonMap.keySet()) {
				if (this.buttonMap.get(key).isSelected()) {
					map.put(key, "true");
				}
			}
			this.m.save("PropertiesFiles/Runner", this.c.compile(map));
			String[] args = new String[] { "-config", "PropertiesFiles/Runner" };
			Preprocessor pre = new Preprocessor();
			Data data = new Data();
			AnalyzerChain analyzerCollection = pre.makePileline(args, data);
			analyzerCollection.run(data);
		});
		panel.add(runButton);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	}
	
	public void setPrompt(String key, Prompt value) {
		this.promptMap.put(key, value);
	}

}
