import java.util.Map;

public class Compiler {
	public String compile(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			sb.append(this.compileElement(key, map.get(key)));
		}
		return sb.toString();
	}

	public String compileElement(String key, String value) {
		if (value.equals("")) {
			return "";
		}
		return key.toLowerCase() + "=" + value + '\n';

	}
}
