import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileSystemPrompt implements Prompt {
	private int promptType;
	
	public FileSystemPrompt(int i) {
		this.promptType = i;
	}

	@Override
	public String getString(String prompt) {
		String str = JOptionPane.showInputDialog(null, prompt);
		return str;
	}

	@Override
	public String getFilePath(String prompt) {
		JFileChooser fileSelector = new JFileChooser();
		fileSelector.setFileSelectionMode(this.promptType);
		fileSelector.showOpenDialog(null);
		File f = fileSelector.getSelectedFile();
		if (f==null) {
			return null;
		}
		return f.getAbsolutePath().replaceAll("\\\\", "/");
	}

}
