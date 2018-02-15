import static org.junit.Assert.*;

import org.junit.Test;

import design.team.nothing.AnalyzerChain;
import design.team.nothing.Data;
import design.team.nothing.Preprocessor;

public class Milestone5Tests {

	@Test
	public void godClassTest() {
		String[] args = new String[] { "-config", "PropertiesFiles/GodClassTest" };
		Preprocessor pre = new Preprocessor();
		Data data = new Data();
		AnalyzerChain analyzerCollection = pre.makePileline(args, data);
		analyzerCollection.run(data);
	}
	
	@Test
	public void godClassTestOurProject() {
		String[] args = new String[] { "-config", "PropertiesFiles/GodClassTest2" };
		Preprocessor pre = new Preprocessor();
		Data data = new Data();
		AnalyzerChain analyzerCollection = pre.makePileline(args, data);
		analyzerCollection.run(data);
	}
	
	@Test
	public void TrainwreckTestFail() {
		String[] args = new String[] { "-config", "PropertiesFiles/TrainWreckTest" };
		Preprocessor pre = new Preprocessor();
		Data data = new Data();
		AnalyzerChain analyzerCollection = pre.makePileline(args, data);
		analyzerCollection.run(data);
	}
	
	@Test
	public void TrainwreckTestPass() {
		String[] args = new String[] { "-config", "PropertiesFiles/TrainWreckTestPass" };
		Preprocessor pre = new Preprocessor();
		Data data = new Data();
		AnalyzerChain analyzerCollection = pre.makePileline(args, data);
		analyzerCollection.run(data);
	}
	

}
