import java.util.Map;

public interface FileManager {
	public Map<String, String> load(String filename);
	public void save(String filename, String text);
}
