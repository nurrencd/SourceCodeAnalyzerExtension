import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
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
	private ButtonGroup group;

	private Map<String, JTextArea> compMap = new HashMap<>();

	public GUIApp(Prompt p, FileManager m, Compiler c) {
		this.p = p;
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
			String response = this.p.getFilePath("Enter the Properties File to load: ", JFileChooser.FILES_ONLY);
			if (response != null) {
				System.out.println(response);
				map = this.m.load(response);
				for (String key : map.keySet()) {
					if (key.equals("uml")||key.equals("sequence")) {
						Enumeration<AbstractButton> enumz = this.group.getElements();
						while (enumz.hasMoreElements()) {
							AbstractButton b = enumz.nextElement();
							System.out.println(b.getText().toLowerCase().trim());
							if (b.getText().toLowerCase().trim().equals(key)) {
								b.setSelected(true);
							}
							else {
								b.setSelected(false);
							}
						}
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
			String filepath = this.p.getFilePath("", JFileChooser.DIRECTORIES_ONLY);
			String response = this.p.getString("Enter save destination");
			if (response != null) {
				System.out.println(filepath + "/" + response);
				Map<String, String> map = new HashMap<String, String>();
				for (String key : this.compMap.keySet()) {
					map.put(key, this.compMap.get(key).getText());
				}
				Enumeration<AbstractButton> enu = this.group.getElements();
				while (enu.hasMoreElements()) {
					AbstractButton b = enu.nextElement();
					if (b.isSelected()) {
						map.put(b.getText().trim().toLowerCase(), "true");
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

		JComponent dirPanel = createPromptPanel("Path", JFileChooser.DIRECTORIES_ONLY);
		JComponent mainPanel = createPromptPanel("Main", JFileChooser.FILES_ONLY);

		JComponent exclusionsPanel = createPropertyPanel("Exclude: ");
		JComponent inclusionsPanel = createPropertyPanel("Classlist: ");
		JComponent patternsPanel = createPropertyPanel("Pattern: ");

		JComponent umlPanel = new JPanel();
		umlPanel.setPreferredSize(new Dimension(800, 50));
		umlPanel.setLayout(new BoxLayout(umlPanel, BoxLayout.X_AXIS));
		JRadioButton umlRadio = new JRadioButton(" UML", true);
		JRadioButton seqRadio = new JRadioButton(" Sequence", false);
		this.group = new ButtonGroup();
		this.group.add(umlRadio);
		this.group.add(seqRadio);
		
		umlPanel.add(umlRadio);
		umlPanel.add(seqRadio);

		panel.add(dirPanel);
		panel.add(Box.createRigidArea(new Dimension(5, 10)));
		panel.add(mainPanel);
		panel.add(Box.createRigidArea(new Dimension(5, 10)));
		panel.add(exclusionsPanel);
		panel.add(Box.createRigidArea(new Dimension(5, 30)));
		panel.add(inclusionsPanel);
		panel.add(Box.createRigidArea(new Dimension(5, 30)));
		panel.add(patternsPanel);
		panel.add(Box.createRigidArea(new Dimension(5, 30)));
		panel.add(umlPanel);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;

	}

	private JComponent createPromptPanel(String title, int type) {
		JComponent mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(800, 50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		JLabel mainLabel = new JLabel(title + ": ");
		JTextArea mainPath = new JTextArea();
		JButton mainButton = new JButton("");
		compMap.put(title.toLowerCase(), mainPath);
		mainButton.addActionListener((e) -> {
			String path = this.p.getFilePath("Select the " + title + " to use.", type);
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
		exclusionsPanel.add(new JLabel(tag));
		JTextArea exclusionsArea = new JTextArea();
		compMap.put(tag.substring(0, tag.length() - 2).toLowerCase(), exclusionsArea);
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
