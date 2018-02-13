import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIApp {
	private Prompt p;
	private FileManager m;
	private Compiler c;
	private JFrame frame;
	
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
		
		JButton saveButton = new JButton("Save Properties");
		panel.add(saveButton);
		
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
		JLabel dirPath = new JLabel();
		JButton dirButton = new JButton("Ex");
		
		dirPanel.add(dirLabel);
		dirPanel.add(dirPath);
		dirPanel.add(dirButton);

		
		JComponent mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(800,50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		
		JLabel mainLabel = new JLabel("Main: ");
		JLabel mainPath = new JLabel();
		JButton mainButton = new JButton("Ex");
		
		mainPanel.add(mainLabel);
		mainPanel.add(mainPath);
		mainPanel.add(mainButton);
		
		panel.add(dirPanel);
		panel.add(mainPanel);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	
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
