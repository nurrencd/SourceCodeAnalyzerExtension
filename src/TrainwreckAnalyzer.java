import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import design.team.nothing.AbstractAnalyzer;
import design.team.nothing.Data;
import design.team.nothing.Relationship;
import design.team.nothing.Relationship.RelationshipType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class TrainwreckAnalyzer extends AbstractAnalyzer{

	private Collection<String> friends = new HashSet<>(); 
	private Pattern pattern;
	
	@Override
	public Data analyze(Data data) {
		pattern = new Pattern(new TrainwreckRenderer());
		
		Collection<SootClass> classes = data.get("classes", Collection.class);
		Scene scene = data.get("scene", Scene.class);
		Collection<Relationship> relationships = data.get("relationships", Collection.class);
		
		for (SootClass c : classes) {
			friends.clear();
			findFriends(c);
			for (SootMethod m : c.getMethods()) {
				checkMethod(m, scene, relationships);
			}
		}
		data.put("trainwreck", pattern);
		return data;
	}
	
	private void checkMethod(SootMethod m, Scene scene, Collection<Relationship> relationships) {
		Collection<String> localFriends = new HashSet<>(friends);
		
		
		CallGraph cg = scene.getCallGraph();
		
		for (Type t : m.getParameterTypes()) {
			localFriends.add(t.toString());
		}
		
		Iterator<Edge> edges = cg.edgesOutOf(m);
		while (edges.hasNext()) {
			Edge e = edges.next();
			SootMethod meth = e.getTgt().method();
		}
		
		//Local Friends found
		if (!m.hasActiveBody()) {
			return;
		}
		boolean classValid = false;
		UnitGraph ug = new ExceptionalUnitGraph(m.getActiveBody());
		
		for (Unit u : ug) {
			if (u instanceof AssignStmt) {
				Value leftOp = ((AssignStmt) u).getLeftOp();
				if (!((AssignStmt) u).containsInvokeExpr()) {
					continue;
				}
				InvokeExpr rightOp = ((AssignStmt) u).getInvokeExpr();
				SootMethod meth = rightOp.getMethod();
				if (meth.isStatic() || meth.isConstructor()) {
					localFriends.add(meth.getDeclaringClass().getName());
				}
			}else if (u instanceof InvokeStmt) {
				InvokeExpr rightOp = ((InvokeStmt) u).getInvokeExpr();
				SootMethod meth = rightOp.getMethod();
				if (meth.isStatic() || meth.isConstructor()) {
					localFriends.add(meth.getDeclaringClass().getName());
				}
			}
		}
//		/System.out.println("Class: " + m.getName() + " Has Friends " + localFriends + "--------------" + m.getDeclaringClass().toString());
		for (Unit u : ug) {
			edges = cg.edgesOutOf(u);
			while (edges.hasNext()) {
				Edge e = edges.next();
				SootMethod meth = e.getTgt().method();
				if (!localFriends.contains(meth.getDeclaringClass().getName())) {
					
					System.out.println("Trainwreck: " + m.getDeclaringClass().getName() + " To " + meth.getDeclaringClass().getName());
					for (Relationship r : relationships) {
						if (r.from.getName().equals(m.getDeclaringClass().getName()) 
								&& r.to.getName().equals(meth.getDeclaringClass().getName())) {
							if (r.type == RelationshipType.DEPENDENCY_ONE_TO_MANY ||
									r.type == RelationshipType.DEPENDENCY_ONE_TO_ONE) {
								classValid = true;
								pattern.addRelationship("trainwreck", r);
							}
						}
					}
				}
			}
			if (u instanceof AssignStmt) {
				Value leftOp = ((AssignStmt) u).getLeftOp();
				Value rightOp = ((AssignStmt) u).getRightOp();
				String str = rightOp.getType().toString();
				if (!scene.containsClass(str)) {
					continue;
				}
				SootClass c = scene.getSootClass(str);
				if (c.isJavaLibraryClass() || c.getName().contains("$") || c.getName().equals("double")) {
					continue;
				}
				if (!localFriends.contains(str)) {
					System.out.println("Trainwreck: " + m.getDeclaringClass().getName() + " To " + str);
					for (Relationship r : relationships) {
						if (r.from.getName().equals(m.getDeclaringClass().getName()) 
								&& r.to.getName().equals(str)) {
							if (r.type == RelationshipType.DEPENDENCY_ONE_TO_MANY ||
									r.type == RelationshipType.DEPENDENCY_ONE_TO_ONE) {
								classValid = true;
								pattern.addRelationship("trainwreck", r);
							}
						}
					}
				}
			}else if (u instanceof InvokeStmt) {
				InvokeStmt invoke = (InvokeStmt) u;
				SootClass clazz = invoke.getInvokeExpr().getMethod().getDeclaringClass();
				if (clazz.isJavaLibraryClass() || clazz.getName().contains("$") || clazz.getName().equals("double")) {
					continue;
				}
				if (!localFriends.contains(clazz.getName())) {
					System.out.println("Trainwreck: " + m.getDeclaringClass().getName() + " To " + clazz.getName());
					for (Relationship r : relationships) {
						if (r.from.getName().equals(m.getDeclaringClass().getName()) 
								&& r.to.getName().equals(clazz.getName())) {
							if (r.type == RelationshipType.DEPENDENCY_ONE_TO_MANY ||
									r.type == RelationshipType.DEPENDENCY_ONE_TO_ONE) {
								classValid = true;
								pattern.addRelationship("trainwreck", r);
							}
						}
					}
				}
			}
		}
		
		if (classValid) {
			pattern.addClass("trainwreck", m.getDeclaringClass());
		}
	}
	
	private void findFriends(SootClass c) {
		friends.add(c.getName());
		
		for (SootField f : c.getFields()) {
			friends.add(f.getType().toString());
		}
	}

}
