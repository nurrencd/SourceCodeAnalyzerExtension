
public class Main {
	public static void main(String[] args) {
		Compiler c = new Compiler();
		FileManager fm = new PropertiesFileManager();
		GUIApp app = new GUIApp(fm, c);
		app.start();
	}
}
