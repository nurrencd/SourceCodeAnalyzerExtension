import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import design.team.nothing.AbstractAnalyzer;
import design.team.nothing.Data;
import design.team.nothing.PatternRenderer;
import design.team.nothing.Relationship;
import design.team.nothing.Relationship.RelationshipType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class GodClassAnalyzer extends AbstractAnalyzer {
	private Pattern pattern;

	@Override
	public Data analyze(Data data) {
		PatternRenderer renderer = new GodClassRenderer();
		this.pattern = new Pattern(renderer);

		Collection<SootClass> classes = data.get("classes", Collection.class);
		for (SootClass c : classes) {
			// check TCC
			float tccValue = calculateTCC(data, c);
			System.out.println(c.getName() + ": " + tccValue+ " tccs");
			
			// calculate dependencies, ATFD
			int dependencyCount = calculateATFD(data, c);
			System.out.println(c.getName() + ": " + dependencyCount + " dependencies");
			
			if (tccValue < .333f && dependencyCount > 5) {
				// this is a god class
				pattern.addClass("god", c);
			}
		}
		data.put("god", pattern);
		return data;
	}

	private float calculateTCC(Data data, SootClass c) {
		float count;
		Scene scene = data.get("scene", Scene.class);
		CallGraph cg = scene.getCallGraph();

		List<Integer> values = new ArrayList<Integer>();

		Collection<String> fieldClasses = new HashSet<String>();
		for (SootField f : c.getFields()) {
			fieldClasses.add(f.getType().toString());
		}

		for (SootMethod m : c.getMethods()) {
			int localMethodCount = 0;
			Iterator<Edge> iter = cg.edgesOutOf(m);
			Edge e;
			while (iter.hasNext()) {
				e = iter.next();
				SootClass target = e.getTgt().method().getDeclaringClass();
				if (fieldClasses.contains(target.getName())) {
					localMethodCount++;
				}
			}
			values.add(localMethodCount);
		}

		count = 0;
		int total = values.stream().mapToInt(Integer::intValue).sum();
		return ((float) total) / fieldClasses.size();

	}
	
	private int calculateATFD(Data data, SootClass c) {
		int count = 0;
		Collection<Relationship> rels = data.get("relationships", Collection.class);
		for (Relationship r : rels) {
			if (r.type == RelationshipType.INHERITANCE || 
					r.type == RelationshipType.IMPLEMENTATION) {
				continue;
			}
			if (r.from.getName().equals(c.getName())) {
				count++;
			}
		}
		return count;
	}

}
