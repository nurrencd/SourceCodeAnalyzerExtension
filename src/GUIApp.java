import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class GUIApp {
	private Prompt p;
	private FileManager m;
	private Compiler c;
	private JFrame frame;
	private String currentFile;
	
	private Map<String, JTextArea> compMap = new HashMap<>();
	
	public GUIApp(Prompt p, FileManager m, Compiler c) {
		this.p = p;
		this.m = m;
		this.c = c;
	}
	
	public void start() {
		this.frame = new JFrame();
		this.frame.setSize(new Dimension(800,800));
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
			String response = this.p.getString("Enter the Properties File to load: ");
			if (response != null) {
				System.out.println(response);
			}
		});
		
		JButton saveButton = new JButton("Save Properties");
		panel.add(saveButton);
		saveButton.addActionListener((e) -> {
			String response = this.p.getString("Enter save destination");
			if (response != null) {
				System.out.println(response);
			}
		});
		
		JButton newButton = new JButton("New Properties");
		panel.add(newButton);
		
		//format
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	}
	
	private JComponent createMiddlePanel() {
		
		JComponent panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JComponent dirPanel = new JPanel();
		dirPanel.setPreferredSize(new Dimension(800,50));
		dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.X_AXIS));
		
		JLabel dirLabel = new JLabel("Directory: ");
		JTextArea dirPath = new JTextArea();
		JButton dirButton = new JButton("Ex");
		compMap.put("Directory", dirPath);
		dirButton.addActionListener((e) -> {
			String path = this.p.getFilePath("Select the Directory to use: ", JFileChooser.DIRECTORIES_ONLY);
			if (path != null) {
				compMap.get("Directory").setText(path);
			}
		});
		
		dirPanel.add(dirLabel);
		dirPanel.add(dirPath);
		dirPanel.add(dirButton);

		
		JComponent mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(800,50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		
		JLabel mainLabel = new JLabel("Main: ");
		JTextArea mainPath = new JTextArea();
		JButton mainButton = new JButton("Ex");
		compMap.put("Main", mainPath);
		mainButton.addActionListener((e) -> {
			String path = this.p.getFilePath("Select the Main class to use: ", JFileChooser.FILES_ONLY);
			if (path != null) {
				compMap.get("Main").setText(path);
			}
		});
		
		mainPanel.add(mainLabel);
		mainPanel.add(mainPath);
		mainPanel.add(mainButton);
		
		JComponent exclusionsPanel = createPropertyPanel("Exclusions: ");
		JComponent inclusionsPanel = createPropertyPanel("Inclusions: ");
		JComponent patternsPanel = createPropertyPanel("Patterns: ");
		
		JComponent umlPanel = new JPanel();
		umlPanel.setPreferredSize(new Dimension(800,50));
		umlPanel.setLayout(new BoxLayout(umlPanel, BoxLayout.X_AXIS));
		JRadioButton umlRadio = new JRadioButton(" UML", true);
		JRadioButton seqRadio = new JRadioButton(" Sequence", false);
		ButtonGroup group = new ButtonGroup();
		group.add(umlRadio);
		group.add(seqRadio);
		umlPanel.add(umlRadio);
		umlPanel.add(seqRadio);
		
		panel.add(dirPanel);
		panel.add(Box.createRigidArea(new Dimension(5,10)));
		panel.add(mainPanel);
		panel.add(Box.createRigidArea(new Dimension(5,10)));
		panel.add(exclusionsPanel);
		panel.add(Box.createRigidArea(new Dimension(5,30)));
		panel.add(inclusionsPanel);
		panel.add(Box.createRigidArea(new Dimension(5,30)));
		panel.add(patternsPanel);
		panel.add(Box.createRigidArea(new Dimension(5,30)));
		panel.add(umlPanel);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	
	}

	private JComponent createPropertyPanel(String tag) {
		JComponent exclusionsPanel = new JPanel();
		exclusionsPanel.setPreferredSize(new Dimension(800,100));
		exclusionsPanel.setLayout(new BoxLayout(exclusionsPanel, BoxLayout.X_AXIS));
		exclusionsPanel.add(new JLabel(tag));
		JTextArea exclusionsArea = new JTextArea();
		compMap.put(tag.substring(0, tag.length()-2), exclusionsArea);
		exclusionsArea.setEditable(true);
		exclusionsPanel.add(exclusionsArea);
		return exclusionsPanel;
	}
	
	private JComponent createBottomPanel() {
		JComponent panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 100));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton newButton = new JButton("Run");
		panel.add(newButton);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
		
	}

}
