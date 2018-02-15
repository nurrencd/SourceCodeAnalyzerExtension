import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileManager implements FileManager {

	@Override
	public Map<String, String> load(String filename) {
		Properties prop = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		Path path = Paths.get(filename);
		FileInputStream in;
		try {
			in = new FileInputStream(path.toFile());
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		prop.keySet().forEach(o -> {
			String str = (String) o;
			map.put(str, prop.getProperty(str));
		});
		return map;
	}

	@Override
	public void save(String filename, String text) {
		try {
			BufferedWriter fr = new BufferedWriter(new FileWriter(filename));
			fr.write(text);
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
