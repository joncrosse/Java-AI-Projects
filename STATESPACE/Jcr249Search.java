package edu.uky.ai.planning.ex;

import java.util.ArrayList;
import java.util.List;


import edu.uky.ai.logic.Conjunction;
import edu.uky.ai.logic.Literal;
import edu.uky.ai.logic.Proposition;
import edu.uky.ai.SearchBudget;
import edu.uky.ai.logic.HashState;
import edu.uky.ai.logic.MutableState;
import edu.uky.ai.planning.Plan;
import edu.uky.ai.planning.Step;
import edu.uky.ai.planning.ss.ForwardNode;
import edu.uky.ai.planning.ss.ForwardSearch;
import edu.uky.ai.planning.ss.StateSpaceProblem;
import edu.uky.ai.util.MinPriorityQueue;

public class Jcr249Search extends ForwardSearch {
    private final MinPriorityQueue<ForwardNode> pqueue = new MinPriorityQueue<>();
    

    public Jcr249Search(StateSpaceProblem problem, SearchBudget budget) {
        super(problem, budget);
    }

    @Override
    public Plan solve() {
    	pqueue.push(root, 0);
        while(!pqueue.isEmpty()) {
            // Pop state off with best heuristic
            ForwardNode current = pqueue.pop();
            // Check if it is a solution.
            if(problem.isSolution(current.plan))
                return current.plan;
            // Consider every possible step...
            for(Step step : problem.steps) {
                // If it's precondition is met in the current state...
                if(step.precondition.isTrue(current.state)) {
                    // Create the state that results from taking this step.
                    MutableState next = new HashState(current.state);
                    step.effect.makeTrue(next);
                    // Add the state results from that step to the frontier.
                    pqueue.push(current.expand(step, next), getHeuristic(current.expand(step, next)));
                }
            }
        }
        // If the queue is empty and we never found a solution, the problem
        // cannot be solved. Return null.
        return null;
    }
    
    private double getHeuristic(ForwardNode node) {
        double H = 0;
        List<Literal> literals = getLiterals(problem.goal);
        for (Literal literal : literals) {
              if(!literal.isTrue(node.state)){
                  H = H + 1;
              }
            }
        return H;
    }
    
    private static List<Literal> getLiterals(Proposition proposition) {
        ArrayList<Literal> list = new ArrayList<>();
        if(proposition instanceof Literal)
            list.add((Literal) proposition);
        else
            for(Proposition conjunct : ((Conjunction) proposition).arguments)
                list.add((Literal) conjunct);
        return list;
    }
}