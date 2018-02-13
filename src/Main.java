
public class Main {
	public static void main(String[] args) {
		Compiler c = new Compiler();
		Prompt p = new FileSystemPrompt();
		FileManager fm = new PropertiesFileManager();
		GUIApp app = new GUIApp(p, fm, c);
		app.start();
	}
}
