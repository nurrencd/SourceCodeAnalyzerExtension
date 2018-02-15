import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileSystemPrompt implements Prompt {

	@Override
	public String getString(String prompt) {
		String str = JOptionPane.showInputDialog(null, prompt);
		return str;
	}

	@Override
	public String getFilePath(String prompt, int selectionMode) {
		JFileChooser fileSelector = new JFileChooser();
		fileSelector.setFileSelectionMode(selectionMode);
		fileSelector.showOpenDialog(null);
		File f = fileSelector.getSelectedFile();
		if (f==null) {
			return null;
		}
		return f.getAbsolutePath();
	}

}
