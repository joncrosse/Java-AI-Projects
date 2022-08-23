package edu.uky.ai.planning.ex;

import edu.uky.ai.SearchBudget;
import edu.uky.ai.planning.ss.ForwardPlanner;
import edu.uky.ai.planning.ss.StateSpaceProblem;

/**
 * A planner that uses simple breadth first search through the space of states.
 * 
 * @author Jonathan Crosse
 */
public class Jcr249 extends ForwardPlanner {

	/**
	 * Constructs a new breadth first search planner. You should change the
	 * string below from "Example" to your ID. You should also change the name
	 * of this class. In Eclipse, you can do that easily by right-clicking on
	 * this file (ExamplePlanner.java) in the Package Explorer and choosing
	 * Refactor > Rename.
	 */
	public Jcr249() {
		super("Jcr249");
	}

	@Override
	protected JchaSearch makeForwardSearch(StateSpaceProblem problem, SearchBudget budget) {
		return new JchaSearch(problem, budget);
	}
}
