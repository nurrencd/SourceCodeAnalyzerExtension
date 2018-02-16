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
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;


/*
 * Metrics and values credited to PMD, with the reference found at https://stackoverflow.com/questions/37389376/pmd-rule-god-class-understanding-the-metrics/37390586
 * Metrics and descriptions found at https://pmd.github.io/pmd-5.8.1/pmd-java/rules/java/design.html#GodClass
 */


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
			
			// calculate dependencies, ATFD
			int complexity = calculateComplexity(data, c);
						
			System.out.println(c.getName() + ": " + complexity + " complexity");
			
			if (tccValue < .3f && dependencyCount > 5 && complexity >= 100) {
				// this is a god class
				pattern.addClass("god", c);
			}
		}
		data.put("god", pattern);
		return data;
	}

	private int calculateComplexity(Data data, SootClass c) {
		Scene scene = data.get("scene", Scene.class);
		int count = 1;
		for (SootMethod m : c.getMethods()) {
			int sum = 1;
			if (!m.hasActiveBody()) {
				continue;
			}
			UnitGraph ug = new ExceptionalUnitGraph(m.getActiveBody());
			for (Unit u : ug) {
				if (u.branches()) {
					sum *= 2;
				}
			}
			count += sum;
		}
		return count;
	}
	
	

	private float calculateTCC(Data data, SootClass c) {
		float count;
		Scene scene = data.get("scene", Scene.class);
		CallGraph cg = scene.getCallGraph();

		if (c.getFieldCount() == 0) {
			return 1;
		}
		
		List<Integer> values = new ArrayList<Integer>();

		Collection<String> fieldClasses = new HashSet<String>();
		for (SootField f : c.getFields()) {
			fieldClasses.add(f.getType().toString());
		}

		for (SootMethod m : c.getMethods()) {
			int localMethodCount = 0;
			if (!m.hasActiveBody()) {
				continue;
			}
			UnitGraph ug = new ExceptionalUnitGraph(m.getActiveBody());
			
			for (Unit u : ug) {
				if (u instanceof AssignStmt) {
					Value leftOp = ((AssignStmt) u).getLeftOp();
					if (!((AssignStmt) u).containsInvokeExpr()) {
						continue;
					}
					InvokeExpr rightOp = ((AssignStmt) u).getInvokeExpr();
					SootMethod meth = rightOp.getMethod();
					if (fieldClasses.contains(meth.getDeclaringClass().getName())) {
						localMethodCount++;
					}
					
				}else if (u instanceof InvokeStmt) {
					InvokeExpr rightOp = ((InvokeStmt) u).getInvokeExpr();
					SootMethod meth = rightOp.getMethod();
					if (fieldClasses.contains(meth.getDeclaringClass().getName())) {
						localMethodCount++;
					}
				}
			}
			values.add(localMethodCount);
		}
		
		count = 0;
		int total = values.stream().mapToInt(Integer::intValue).sum();
		System.out.println(total + "   " + fieldClasses.size());
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
