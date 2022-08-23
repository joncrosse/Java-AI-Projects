package edu.uky.ai.sat.ex;

import java.util.Random;
import java.util.ArrayList;

import edu.uky.ai.sat.Assignment;
import edu.uky.ai.sat.Clause;
import edu.uky.ai.sat.Solver;
import edu.uky.ai.sat.Value;
import edu.uky.ai.sat.Variable;

public class JCR249 extends Solver {

	private final Random random = new Random(0);

	/**
	 * Constructs a new random SAT solver. You should change the string below from
	 * "example" to your ID. You should also change the name of this class. In
	 * Eclipse, you can do that easily by right-clicking on this file
	 * (ExampleSolver.java) in the Package Explorer and choosing Refactor > Rename.
	 */
	public JCR249() {
		super("JCR249");
	}

	@Override
	public boolean solve(Assignment assignment) {
		if (assignment.problem.variables.size() == 0)
			return assignment.getValue() == Value.TRUE;
		else {
			// Randomly assign every variable at the start
			randomAssign(assignment);
			// Keep looping until the chosen clause is true
			while (assignment.getValue() != Value.TRUE) {
				// Randomly select a clause from the list of false clauses
				Clause c = randClause(assignment);

				while (assignment.getValue(c) != Value.TRUE) {
					// Randomly flip a variable
					int falseClause = assignment.countFalseClauses();
					Variable randVar = c.literals.get(random.nextInt(c.literals.size())).variable;

					if (assignment.getValue(randVar) == Value.TRUE)
						assignment.setValue(randVar, Value.FALSE);
					else
						assignment.setValue(randVar, Value.TRUE);
				}
			}
			return true;
		}
	}

	// Function to check if the flipped variable is beneficial.
	private int countNew(Assignment assignment, Variable var) {
		Value currentVal = assignment.getValue(var);
		Value opp = currentVal.negate();
		assignment.setValue(var, opp);
		int n = assignment.countFalseClauses();
		assignment.setValue(var, currentVal);
		return n;
	}

	private void randomAssign(Assignment assignment) {
		double probability = .3;
		for (Variable v : assignment.problem.variables) {
			if (random.nextDouble() > probability) {
				assignment.setValue(v, Value.TRUE);
			} else {
				assignment.setValue(v, Value.FALSE);
			}
		}
		return;
	}

	private final Clause randClause(Assignment assignment) {
		// Array to hold all false clauses to choose from
		ArrayList<Clause> clausesToSolve = new ArrayList<>();
		for (Clause clause : assignment.problem.clauses)
			if (assignment.getValue(clause) == Value.FALSE)
				clausesToSolve.add(clause);
		// Choose a random clause from the array and return it
		return clausesToSolve.get(random.nextInt(clausesToSolve.size()));
	}
}